from django.conf.urls.defaults import *
from django.conf import settings
from django.contrib import admin

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    # (r'^newspet/', include('newspet.foo.urls')),
    url(r'^site-media/(?P<path>.*)$', 'django.views.static.serve', {'document_root': settings.MEDIA_ROOT}),#(r'^blog/', include('blog.urls')),
    (r'^feed/', include('newspet.feed.urls')),
    url(r'^$', 'feed.views.home', name="home"),
    url(r'^accounts/logout/$', 'django.contrib.auth.views.logout_then_login', name = 'np_logout'),
    # Uncomment the admin/doc line below and add 'django.contrib.admindocs' 
    # to INSTALLED_APPS to enable admin documentation:
    # (r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    (r'^admin/(.*)', admin.site.root),
)
