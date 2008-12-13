from django.db import models
from django.core.urlresolvers import reverse
import datetime

# Create your models here.
class Post(models.Model):
    title = models.CharField( max_length = 255 )
    slug = models.SlugField( )
    content = models.TextField( )
    short_blurb = models.CharField( max_length = 255, null = True, blank = True )
    is_news = models.BooleanField( )
    date_added = models.DateTimeField( default = datetime.datetime.now )
    
    def get_absolute_url(self):
        """docstring for get_absolute_url"""
        return reverse( 'sb_post_detail', args = [self.slug] )
    
    def __unicode__(self):
        return self.title
