# Reminders Builder 

<img src='RemindersBuilder.png'>

## A Java Swing Desktop App that helps 'build' and create text reminders with efficiency and maintenance

The app allows users to easily create and update text reminders. It provides a user-friendly guide with predefined text-fields and a format for creating and managing text reminders. Users can then paste these reminders into their notes or any platform where they find it useful for timely reminders.

A sample where an output text reminder might be useful can be seen in the image(s) below:

<div style='
  display: flex;
  flex-direction: row;
  object-fit: cover;
  column-gap: 10px;
  max-width: 50%;
  max-height: 30%;
'>
  <img src='messengerReminders.png'>
  <img src='googlekeepReminders.png'>
</div>

## Instructions
### To use the app
* you can download and run the RemindersBuilder.exe file under the dist folder if you want to use the app, 
* if the exe file doesn't open, you can try to download the jar file and run that instead, if it still doesn't open you can try to troubleshoot by downloading jarfix.exe on the web, enable jarfix, then try to run it again
* if it still doesn't work, just clone the whole repository on your IDE client and try to compile it on your end.
### How to use the App
* When you open the exe file, the app will create a .txt file on the same directory that will serve as the user database file
* The creation of Reminders is straightforward, you can press the create button to create a brand new reminders, fill in all the text fields, choose the format you want, the style of the bullet, the title, etc. and just press the CREATE REMINDER at the bottom. You will be directed The output page, you can now copy and save your reminders. When you press copy the reminders will be copied as text on your clipboard and you can paste it anywhere you want. When you press the save button the created reminders will be saved onto the database and in the collections page where you can see all your created reminders. 
* To update a reminder that you are using, just simply click the UPDATE button and make your desired changes and click the UPDATE REMINDERS button and save your reminders.

## Features still in development
* a need for a time chooser UI (currently, only data chooser is available per bullet item)
* an SQL database (the database currently runs on a .txt file)

## Images

### Home Page
<img src='RemindersBuilder_homepage.png'>

### Create Page
<img src='RemindersBuilder_createpage.png'>

### Update Page
<img src='RemindersBuilder_updatepage.png'>

### Collections Page
<img src='RemindersBuilder_collectionspage.png'>