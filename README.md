# Triis

This project is a social media website developed with Vaadin and Spring.

The main premise of the application is a point system in which original content is rewarded.

## How it works

Any user can make a post, however, not all posts can generate points. In order to generate points you have to invest your own points first. Let's call the points
that you invest and the points you generate *Type 1 Points*. 

Now if you invest Type 1 Points on one of your posts it can now generate the same Type 1 Points whenever it is reposted:

Other users can repost your post. If they decide to use *Type 2 Points* when reposting, they create a Pointed Repost. This means that the new repost they created on their profile can also generate Type 2 Points.

The main difference between Type 1 and Type 2 Points is that you can constantly generate the first kind of points if you are being reposted, while the second one has a weekly refill. This means that you can only spend points on repost 10 times a week.

The objective of this limit is to prevent users repost absolutely everything they see with the hopes of generating Type 1 Points this way.

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `services` package in `src/main/java` contains the controllers with methods called by the views.
- `repositories` package in `src/main/java` contains the methods to change the entities in the database, one for each entity.
- `entities` package in `src/main/java` contains the classes that model each of the entities on the database.
- `themes` folder in `frontend/` contains the custom CSS styles.
