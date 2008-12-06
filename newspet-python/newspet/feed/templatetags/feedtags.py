from django import template
from feed.forms import CategoryChangeForm

register = template.Library()

@register.inclusion_tag('feed/categories.html')
def show_categories(user):
    categories = user.category_set.all()
    return {'categories': categories}



class FormNode(template.Node):
    def __init__(self, user, category):
        self.user = template.Variable(user)
        self.category = template.Variable(category)
    
    def render(self, context):
        try:
            return str(CategoryChangeForm(self.user.resolve(context), self.category.resolve(context).id)['category']).replace('\n', ' ')
        except template.VariableDoesNotExist:
            return ''

@register.tag(name='get_category_select')
def do_get_category_select(parser, token):
    try:
        _tag, user, category = token.split_contents()
    except ValueError:
        raise template.TemplateSyntaxError, "Dont be stupid, {%% get_category_selet <user> <category> %%} that easy"
    return FormNode(user, category)