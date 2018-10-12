# Skatespot-Organizer

## Introduction
Welcome to our Skatespot Organizer app! The goal of this app is to provide fellow skateboarders around the world with an easy way to catalogue their favorite places ("spots") to skateboard. An integral part of street skateboarding consists of finding spots, often with unique architecture, to skate. Skateboarders spend countless hours searching for new spots, deciding which spots to skate on a given day, and trying to find out about spots from others. Despite the significant roll of spot searching, cataloguing, and sharing, the ways that skateboarders resort to doing this is surprisingly primitive; most skateboarders have nothing more than a mental library of spots, remembering only vaguely the general vicinity of and layout of each spot. The more organized skaters may have some photos with geodata buried in their photo roll on their phone or embedded in a thread of a chat app. Consequently, returning to and sharing spots often is not always as easy as it could be.

The motivation behind making this app comes from the observation that there is a shortage of good apps that allow skateboarders to organize and share spots in an easy way. While there are a few "spot apps" out there, most of the ones we've used suffer from one or more of the following problems:

1). Inability to keep spots private: While sharing is what happens to most spots after they are discovered, skateboarders often prefer to share certain spots with only a select few people. The reason is that spots can be become "busts" (non-skateable) if they become too popular. So, more than altruistic sharing, what happens mainly is reciprocal sharing (trading of spots) between friends and acquaintances. Thus, while our app will allow users to share spots, it won't force them to. Skatespot Organizer will allow users to have a private collection of spots as well as the option to share spots (in a user-controlled way) with the larger skate community.

2). Spot search/retrieval functionality is limited: Some apps allow users to see all spots within a given radius of a specific city and/or their current location, or based on the spots' attributes (name, obstacles, etc.), however, we are yet to see an app that integrates all of these search options together in one app. Since spot search/retrieval will be a primary function of this app, we hope to make this task as user-friendly as possible.

3). Too general: General-purpose apps such as Google Maps aren't tailored (or configurable) enough to skateboarding's unique and specific requirements when it comes to cataloguing spots. While it is possible to place and save makers and information on a via these general-purpose map apps, it is often difficult create collections of spots in a uniform way that would allow a skater to quickly access their saved spots and have the all the spot info they want in a just a click or two. As for other special-purpose spot apps, many of these encompass multiple extreme spots and force these (often quite disparate) communities of users to share the same platform. This is nice if you are indeed a jack of all extreme sports, however, from our personal experience, we've seen that many users are really only interested in one sport, and would at least like to have the option of limiting the app to their sport of interest.

4). Sloppiness: The user experience offered by most of the apps we've seen so far is degraded in part by simple spelling mistakes, awkward user controls, etc. While we understand that it would be impossible to completely satisfy all users in their user experience, we'd like our app to have a clean user interface and be both easy and intuitive to use.

## Enter the Skatespot Organizer app...

The first version of our app will provide four main pieces of functionality:

1). Spot storage: First and foremost, the user must be able to store spots in the app. A spot will consist of a spot name, geographical coordinates, a brief description, and a small number of photos.

2). Spot editing: Likewise, the user will also want to be able to edit (i.e., change name, description, photo, etc.) and delete existing spots.

3). Spot search/retrieval: There will be two channels for a user to search for and retrieve saved spots:

  * Quick search: The search bar at the top of the app allows the user to perform a search based on either spot name or location name (i.e., "Brooklyn Banks", "New York City", "Spain").
  * Advanced search: Via the sidebar navigation drawer, the user will be able to select Advanced Search to order to further refine the search parameters to consider spot type, radius from a given location, bust factor, and more.
  
4). Spot viewing: There are three viewing modes:

  * Map view: When in this viewing mode, the user will see a map displaying either all of their saved spots or the results of their search as highlighted map markers. The map also provides control buttons which allow the user to zoom in/out, zoom into their current location, and open the spot's location in Google Maps (in order to receive directions). Clicking on a marker pops an info window displaying more info about the spot (name, description), and clicking the info window brings the user to the spot's detail view.
  * List view: This viewing mode allows the user to view either all of their saved spots or the results of their search in scrollable list format. Here, the user can quickly see the name, main photo, and description of the spots. There will also be options to sort the list by various factors such as rating, name, distance, etc. Clicking on a spot list item takes the user to the spot's detail view.
  * Detail view: When in this viewing mode, the user will be able to see all of the spot's details in unabbreviated form. This will include a gallery of photos, video clips, a description, rating, bust factor, and more.
