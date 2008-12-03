from django import forms
from models import *

class CategoryForm(forms.ModelForm):
    name = forms.CharField(max_length = 63)
    class Meta:
        model = Category
        exclude = ('is_trash', 'owner')

TRAINING_CHOICES = (('Pre-Trained Categories', 0),
                    ('Word or Phrase', 1),
                    ('Load Batch Files', 2),)

class InitTrainingForm(forms.Form):
    method = forms.ChoiceField( choices= TRAINING_CHOICES,
                                widget = forms.RadioSelect())

class TrainedCategoryForm(forms.Form):
    def __init__(self):
        """docstring for __init__"""
        self.fields['categories'].choices = [(ptc, ptc.id) for ptc in PreTrainedCategory.objects.all()]
    
    categories = forms.ChoiceField()
    
class PhraseForm(forms.Form):
    phrase = forms.CharField(max_length = 127)

class BatchFileForm(forms.Form):
    files = forms.CharField(max_length = 500)

class FeedForm(forms.ModelForm):
    class Meta:
        model = Feed
        exclude = ('subscriber')
    