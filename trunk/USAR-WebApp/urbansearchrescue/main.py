import os
from google.appengine.ext.webapp import template

import cgi

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

from datetime import datetime


def require_login(handler):
    user = users.get_current_user()
    if not user:
        handler.redirect(users.create_login_url(handler.request.uri))
       

class Location(db.Model):
    date_created = db.DateTimeProperty(auto_now_add=True) # the date the location was entered into the database

    # The person entering the data
    updated_by = db.UserProperty()
    date_updated = db.DateTimeProperty()


    
    # The person on the field. field data (data around the X mark on the wall)
    num_people = db.IntegerProperty() # default Null
    condition = db.StringProperty(multiline=False) # default Null
    date_found = db.DateTimeProperty() # default Null
    agency = db.StringProperty(multiline=False) # default Null


# in case we need multiple images per location, and each has some sort of metadata
class LocationImage(db.Model):
    location = db.ReferenceProperty(Location, collection_name='images', required=False) # should be required eventually
    image = db.BlobProperty()
    def url(self):
        return 'img?img_id=%s' % self.key()


# Just for testing purposes
class UploadLocationImageForm(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""<html><body>
                  <form action="/upload" enctype="multipart/form-data" method="post">
                    <div>Location ID:<input type="text" name="location_id"/></div>
                    <div><input type="file" name="img"/></div>
                    <div><input type="submit" value="Send Pic"></div>
                  </form>
                </body>
              </html>""") 


class UploadLocationImage(webapp.RequestHandler):
    def post(self):
        require_login(self)

        locationimage = LocationImage()
        image = self.request.get("img")
        locationimage.location = db.get(self.request.get("location_id"))
        locationimage.image = db.Blob(image)
        locationimage.put()
        self.redirect('/uploadform')


class MainPage(webapp.RequestHandler):
    def get(self):
        self.redirect('/locationinfo')
        return

# Just for testing purposes
class NewLocation (webapp.RequestHandler):
    form = """%i locations <html><body>
                  <form action="/newlocation" enctype="multipart/form-data" method="post">
                    <div><input type="submit" value="New Location"></div>
                  </form>
                </body>
              </html>""" % Location.all().count()

    def post(self): # just make a new location
        Location().put()
        self.response.out.write(self.form)

    def get(self):
        self.response.out.write(self.form)


class UpdateLocation (webapp.RequestHandler):
    def post(self):
        require_login(self)

        location = db.get(self.request.get('location_id'))

        location.updated_by = users.get_current_user()
        location.date_updated = datetime.now()

        if self.request.get('num_people'):
            location.num_people = int(self.request.get('num_people'))
        if self.request.get('condition'):
            location.condition = self.request.get('condition')
        if self.request.get('date_found'):
            location.date_found = self.request.get('date_found')
        if self.request.get('agency'):
            location.agency = self.request.get('agency')

        location.put()

        self.redirect('/locationinfo?location_id=%s' % location.key())

class LocationInfo (webapp.RequestHandler):
    def get(self):
        if self.request.get('location_id'):
            self.showlocation()
        else:
            self.listlocations()

    def showlocation(self):
        location = db.get(self.request.get("location_id"))
        path = os.path.join(os.path.dirname(__file__), 'locationinfo.html')
        self.response.out.write(template.render(path, {"location": location}))

    def listlocations(self):
        path = os.path.join(os.path.dirname(__file__), 'listlocations.html')
        self.response.out.write(template.render(path, {"locations": Location().all()}))



class ImageServe (webapp.RequestHandler):
    def get(self):
        locationimage = db.get(self.request.get("img_id"))
     
        if locationimage.image:
            self.response.headers['Content-Type'] = "image/jpg"
            self.response.out.write(locationimage.image)
        else:
            self.error(404)


application = webapp.WSGIApplication(
                                     [('/', MainPage),
                                      ('/uploadform', UploadLocationImageForm),
                                      ('/upload', UploadLocationImage),
                                      ('/img', ImageServe),
                                      ('/newlocation', NewLocation),
                                      ('/locationinfo', LocationInfo),
                                      ('/updatelocation', UpdateLocation)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
