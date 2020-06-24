This directory is not part of the IntelliJ project.  Its purpose is to package AutoGrader2 for distribution as a macOS app.

Execute the shell script build_app.sh to package the latest compilation of AutoGrader2.  Prior to running the script, be sure that you have successfully executed maven javafx:compile and javafx:jlink.

The directory _build_app contains some of the resources used for building the app.  Among these is a compressed skeleton macOS app, AG2AppTemplate.tgz.  The directory also contains a symbolic link to the source file AutoGraderApp.java.

The directory Release contains the macOS binaries.  This directory is organized into sub-directories according to the release version number.

The build_app.sh script does the following:
1) it attempts to auto-detect the release number by examining the AutoGraderApp.java source file through the symbolic link in the _build_app directory.
2) it creates a sub-directory in the Release directory with a name corresponding to the release number
3) it uncompresses the template app in the _build_app directory to the sub-directory created in step 2
4) it copies the "image" folder from the IntelliJ image target directory to the template in the Release sub-directory
5) it compresses the newly created application
6) it deletes the app, leaving only its compressed .tgz image
