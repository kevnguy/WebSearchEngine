import os
import re
import scrapy
import logging
import argparse
from trafilatura import extract
from datetime import datetime
from scrapy.crawler import CrawlerProcess
from scrapy.utils.project import get_project_settings

log = logging.getLogger(__name__)

class WikiSpider(scrapy.Spider):
    def __init__(self, file=None):
        if file:
            with file as f:
                self.start_urls = [url.strip() for url in f.readlines()]
        else:
            self.start_urls = ['https://en.wikipedia.org/wiki/Dog']        
            
    name = "wiki"
    allowed_domains = ['wikipedia.org']

    def parse(self, response):
        log.info('Parse function called on %s on spider %s', response.url,self.name)
        #pass search to api to fetch main content 
        content = extract(response.body,favor_recall=True,include_tables=False,include_comments=False,no_fallback=True)
        content = re.sub("[\[].*?[\]]", '', content)
        modRaw = response.css('#footer-info-lastmod').re_first('\d.*\d')
        lastMod = datetime.strptime(modRaw,"%d %B %Y, at %H:%M")
        # pattern to filter out wikipedia namespaces
        pattern = ('^\/wiki\/'
                   '(?!(?:File:|Help:|User:|Talk:|Draft:|Media:|Module:|Portal:|Special:|Category:'
                   '|Template:|MediaWiki:|TimedText:|Wikipedia:'
                   '|User_talk:|Wikipedia_talk:|File_talk:|MediaWiki_talk:|Template_talk:|Help_talk:'
                   '|Category_talk:|Portal_talk:|Draft_talk:|TimeText_talk:|Module_talk:)).*$')
        validLinks = response.css('#content * a::attr(href)').re(pattern)    

        yield {
            'url': response.url,
            'title': response.css('title::text').get(),
            'lastMod': lastMod,
            'TOC': response.css('.mw-headline::attr(id)').getall(),
            'mainText': content.split('\n- ^ ',1)[0],
            'boldText': ' '.join(response.css('#content * b::text').re('[^0-9,()]{2,}')),
            'italicText': ' '.join(response.css('#content * i::text').re('[^0-9,()]{2,}')),
            'img': response.css('.infobox * img::attr(src), .infobox-full-data * img::attr(src),' 
                                +' .thumbinner * img::attr(src), .sidebar-image * img::attr(src)').get(),
            'links': ' '.join(validLinks)
        }

        #follow valid links
        # yield from response.follow_all(validLinks, callback=self.parse)

def main():
    parser = argparse.ArgumentParser(description="Crawler options. To configure mongoDB connection, please edit project settings.py")
    parser.add_argument('inputfile', metavar='input_file', nargs='?', type=argparse.FileType('r'), help="Input seed file of urls")
    parser.add_argument('-p', '--num_page', metavar='#', type=int, help="Page limit on crawl (default = 10000)")
    parser.add_argument('-d', '--depth', metavar='#', type=int, help="Depth limit on crawl (default = 6)")
    parser.add_argument('-o', '--output', metavar='dir', type=str, 
            help="Output directory to store scraped pages. Creates directory if doesn't exist")
    parser.add_argument('-m', '--mongo', action='store_true', help="Option to enable mongo pipeline. Configure settings in settings.py")
    parser.add_argument('-s', '--silent', action='store_true', help="Option to supress logging to INFO")
    args = parser.parse_args()

    settings = get_project_settings()
    if args.output:
        os.makedirs(f"{args.output}", exist_ok=True)
        #using scrapy feeds to output to file. Item count 1 = 1 item per file
        settings['FEED_EXPORT_BATCH_ITEM_COUNT'] = 1
        settings['FEEDS'] = {
           f"{args.output}/%(batch_id)d-page%(batch_time)s.json" : {
               'format' : 'json',
               'encoding': 'utf8',
               'item_export_kwargs': {
                    'export_empty_fields': True,
                }
           }
        }
    if args.depth is not None:
        settings['DEPTH_LIMIT'] = args.depth
    if args.num_page is not None:
        settings['CLOSESPIDER_PAGECOUNT'] = args.num_page
    if args.silent:
        settings['LOG_LEVEL'] = "INFO"
    if args.mongo:
        settings['ITEM_PIPELINES']['wikipedia_scraper.pipelines.MongoDBPipeline'] = 100

    process = CrawlerProcess(settings)
    process.crawl(WikiSpider,args.inputfile)
    process.start()

if __name__ == "__main__":
    main()


