from django.conf.urls.defaults import *

urlpatterns = patterns('',
    url(r'^item/(\d+)/$', 'feed.views.item', name="f_item"),
    url(r'category/(\d+)/$', 'feed.views.category', name="f_category"),
    url(r'manage/categories/$', 'feed.views.manage_categories', name="manage_catagories"),
    url(r'manage/feeds/$', 'feed.views.manage', name="manage_feeds"),
)