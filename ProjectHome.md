![http://dl.getdropbox.com/u/191765/newspet_logo.jpg%20%40%2066.7%25%20%28NewsPet%2C%20RGB_8%29.png](http://dl.getdropbox.com/u/191765/newspet_logo.jpg%20%40%2066.7%25%20%28NewsPet%2C%20RGB_8%29.png)

Iowa State University, Com S 472 project: NewsPet

# Summary #
NewsPet is a news-reader web application that categorizes RSS news items using a trainable engine.

# Goals #
  * The application monitors a configurable set of RSS feeds for each user.
  * For each news item retrieved, the application will place a link to it into one of several user-specified categories, depending on text content.
  * There will always be a "trash" category that serves as the category for otherwise uncategorized items.
  * The user is able to view each category's set of news items via a web interface.
  * The user is able to re-categorize news items, (which serves as providing performance feedback to the application).

(Application overview):

![http://news-pet.googlecode.com/svn/trunk/Documentation/NewsPetDiagram.png](http://news-pet.googlecode.com/svn/trunk/Documentation/NewsPetDiagram.png)

[Architecture sketches](Architecture.md)

# Approach #
## Design ##
### Categorization ###
For each read news item, a vector of per-category probabilities is retrieved from a Naive Bayes classifier. The most probable category is then assigned to the item, provided it meets some lower bound (depending on the number of categories).

### Feedback ###
For every item in every category, there will be the ability to say that the article is accurately categorized and the system should be more confident in accepting documents like this one, or that the article should be categorized differently.

### Logistics ###
For the main categorization portion of the application, we are utilizing a Naive Bayes classifier, (in particular, we are using [Mallet](http://mallet.cs.umass.edu/) as a library in our application).

We are using Java for the classification and classifier training services, and [Django](http://www.djangoproject.org), a python web framework, as our front end web-based UI.

[Informa](http://informa.sourceforge.net/) is used as an RSS retriever and parser.

## Testing ##
We have tested the classification logic of our application with data from the [Reuters 21578](http://kdd.ics.uci.edu/databases/reuters21578/reuters21578.html) collection.

# Report #
The most recent draft of the report for this project can be viewed [here](http://news-pet.googlecode.com/svn/trunk/Documentation/report/report.pdf).

Presentation slides are viewable [here](http://docs.google.com/Presentation?id=ddnqnc67_0hkbjxpf6).