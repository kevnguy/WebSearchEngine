# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html

# useful for handling different item types with a single interface
from itemadapter import ItemAdapter
from scrapy.exceptions import DropItem
import pymongo
import json
import logging

log = logging.getLogger(__name__)

class MongoDBPipeline(object):
    def __init__(self, mongo_uri, mongo_db, mongo_collection):
        self.mongo_uri = mongo_uri
        self.mongo_db = mongo_db
        self.mongo_collection = mongo_collection

    @classmethod
    def from_crawler(cls, crawler):
        ## pull in information from settings.py
        return cls(
            mongo_uri = crawler.settings.get('MONGODB_URI', 'mongodb://localhost:27017'),
            mongo_db = crawler.settings.get('MONGODB_DB', 'WikiScrape'),
            mongo_collection = crawler.settings.get('MONGODB_COLLECTION', 'Pages')
        )

    def open_spider(self, spider):
        self.client = pymongo.MongoClient(self.mongo_uri)
        self.db = self.client[self.mongo_db]
        self.collection = self.db[self.mongo_collection]
        #create index on url to keep unique
        self.collection.create_index([("url", pymongo.ASCENDING)],unique = True)
        self.collection.create_index([("title", pymongo.ASCENDING)],unique = True)

    def close_spider(self, spider):
        self.client.close()

    def process_item(self, item, spider):
        try:
            # if self.collection.count_documents({'title': item['title']}):
            #     raise DropItem("Duplicate found... Dropping item")
            # else:
            self.collection.update_one({'title': item['title']}, {"$set": item }, upsert=True)
        except Exception:
            # log.exception(f"Error adding item to DB with url: {item['url']}")
            raise DropItem("Error adding item to DB with url: %s" % item['url'])
        return item

class StoreLocalPipeline:
    def __init__(self, output_dir):
        self.output_dir = output_dir

    @classmethod
    def from_crawler(cls, crawler):
        return cls(
            output_dir = crawler.settings.get('output_dir')
        )

    def process_item(self, item, spider):
        page = item['url'].split('/')[-1]
        filename = '%s/wiki-%s.json' % (self.output_dir,page)
        with open(filename, 'w') as f:
            json.dump(item,f,default=str)
        return item

class WikipediaScraperPipeline:
    def process_item(self, item, spider):
        return item
