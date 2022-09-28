# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

from scrapy import Item, Field
from scrapy.loader.processors import TakeFirst, Join, Compose

import scrapy

class Article(scrapy.Item):
    # define the fields for your item here like:
    # name = scrapy.Field()
    # url = Field(output_processor=TakeFirst())
    # title = Field(output_processor=TakeFirst())
    # lastMod = Field()
    # TOC = Field()
    # mainText = Field(
    #     output_processor=Compose(lambda v: filter(None, v), Join(''))
    # )
    # boldText = Field()
    # italicText = Field()
    # img = Field(output_processor=TakeFirst())
    # links = Field()

    pass
