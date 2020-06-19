The directory AutoGrader2.app is a shell application only.  To create a working application, you must first successfully create a self-contained application using maven jlink.  This will create an image in the directory {project_directory}/target.  Here are the steps:

1) make a copy of AutoGrader2.app in a location outside of the git repository
2) copy the image directory found under {project_directory}/target
3) right click on the copy of AutoGrader2.app and show its contents.
4) paste the image directory in the Contents/macOS/ directory of AutoGrader2.app