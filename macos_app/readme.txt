The directory AutoGrader2.app is a shell application only.  To create a working application, you must first successfully create a self-contained application using maven jlink.  This will create an image in the directory {project_directory}/target.  Here are the steps:

1) create a directory in the Releases folder using the version number as the directory name.  This is our build directory.
2) uncompress AutoGrader2.app.zip into the build directory created in step 1.  This should result in a "shell" AutoGrader2.app directory.
3) copy the image directory found under {project_directory}/target
4) right click on the copy of AutoGrader2.app in the build directory and show its contents.
5) paste the image directory in the Contents/macOS/ directory of AutoGrader2.app
6) delete any existing AutoGrader2.app.zip file it exists in the build directory
7) compress AutoGrader2.app to create a new zip file
8) delete AutoGrader2.app or move it to a location outside of the git directory