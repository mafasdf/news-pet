from django.conf.urls.defaults import *

urlpatterns = patterns('',
    url(r'^item/(\d+)/$', 'feed.views.item', name='f_item'),
    url(r'category/(\d+)/$', 'feed.views.category', name='f_category'),
    url(r'manage/categories/$', 'feed.views.manage_categories', name='f_manage_categories'),
    url(r'manage/categories/edit/(?P<category_id>\d+)/$', 'feed.views.manage_categories', name='f_edit_category'),
    url(r'manage/feeds/$', 'feed.views.manage_feeds', name='f_manage_feeds'),
    url(r'manage/feeds/edit/(?P<feed_id>\d+)/$', 'feed.views.manage_feeds', name = 'f_edit_feed'),
    url(r'manage/feeds/delete/(\d+)/$', 'feed.views.delete_feed', name='f_delete_feed'),
    url(r'dissaprove/item/(\d+)/$', 'feed.views.love_item', name='f_love_item'),
    url(r'approve/item/(\d+)/$', 'feed.views.hate_item', name='f_hate_item'),
    url(r'/move/(\d+)/', 'feed.views.move', name='f_move_item'),
)