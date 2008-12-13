from atom import Feed
from django.core.urlresolvers import reverse
from django.conf import settings
from django.contrib.sites.models import Site
from simpleblog.models import Post
from django.template.defaultfilters import linebreaks, escape, capfirst

ITEMS_PER_FEED = getattr(settings, 'ATOM_ITEMS_PER_FEED', 20)

class BlogFeed(Feed):
    feed_id = "http://%s/syndication/blog/" % Site.objects.get_current().domain
    
    feed_title = "Feedalizer.net Blog Latest Entries"
    
    def feed_updated(self):
        return Post.objects.latest('date_added').date_added
    
    def feed_links(self):
        return ({'href': self.feed_id},)
    
    def items(self):
        return Post.objects.order_by('-date_added')[:ITEMS_PER_FEED]
    
    def item_id(self, item):
        return "http://%s%s" % (
            Site.objects.get_current().domain,
            item.get_absolute_url(),
        )
    
    def item_title(self, item):
        return item.title
    
    def item_updated(self, item):
        return item.date_added
    
    def item_published(self, item):
        return item.date_added
    
    def item_content(self, item):
        return {"type" : "html", }, linebreaks(item.content)
    
    def item_links(self, item):
        return [{"href" : self.item_id(item)}]
    
    def item_authors(self, item):
        return [{"name" : "The Feedalizer.net Team"}]