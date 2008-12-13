from django.conf.urls.defaults import *
from simpleblog import views

urlpatterns = patterns('',
    url(r'^detail/([A-Za-z_-]+)/$', views.post_detail, name = 'sb_post_detail'),
    url(r'^$', views.post_list, name = 'sb_post_list'),
   # url(r'^channels/([A-Za-z_-]+)/(subscribe|unsubscribe)/$', 'views.subscribe_unsubscribe', name = 'f_channel_subscribe'),
    
)