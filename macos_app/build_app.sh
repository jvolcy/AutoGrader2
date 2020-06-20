#!/bin/bash

#The directory AutoGrader2.app is a shell application only.  To create a working application, you must first successfully create a self-contained application using maven jlink.  This will create an image in the directory {project_directory}/target.  Here are the steps:
#
#1) create a directory in the Releases folder using the version number as the directory name.  This is our build directory.
#2) uncompress AutoGrader2.app.zip into the build directory created in step 1.  This should result in a "shell" AutoGrader2.app directory.
#3) copy the image directory found under {project_directory}/target
#4) right click on the copy of AutoGrader2.app in the build directory and show its contents.
#5) paste the image directory in the Contents/macOS/ directory of AutoGrader2.app
#6) delete any existing AutoGrader2.app.zip file it exists in the build directory
#7) compress AutoGrader2.app to create a new zip file
#8) delete AutoGrader2.app or move it to a location outside of the git directory

# =========================================================================================
# This script attempts to replicate the steps above
# =========================================================================================


#verify that the correct number of arguments were provided
#If 0 parameters are provided, we will attempt to auto-version.
#Auto-versioning is possible only if a symbolic link to the file AutoGraderApp.java
#exists in the _build_app directory.  The script attempts to extract the version
#information from this file.'

#cd to the directory of the script (this permits launching from Finder)
cd `dirname $0`

if [ "$#" -eq 0 ]; then
	echo "Attempting to auto-version..."
	version=`grep -e "version =" _build_app/AutoGraderApp.java | awk -F'"' '{print $2}'`
	if [ "$version" == "" ]; then
		echo "Auto-versioning failed."
		exit 0
	fi
    #verify the auto-detected version #
    echo "Version $version detected."
    read -p "Is ths correct? (y/n)"

    if [ "$REPLY" != "y" ]; then
    echo "Aborting..."
    exit 0
    fi
#if 1 argument is provided, assume this is the version #
elif [ "$#" -eq 1 ]; then
	version=$1
else
	echo "Usage: $0 [version #]"
    echo "Example: $0 2.1.1"
    echo "If no version # is provided, the srcipt will attempt to auto-detect the version #.  This is the preferred method."
	exit 0
fi


#build the destination directory
target_dir="Releases/$version"

#remind the user that the program should be linked before running this script
echo ""
echo ">>>>> building AutoGrader2 app v. $version <<<<<"
echo ""
echo "************************* NOTICE *************************"
echo " You should have successfully executed maven javafx:jlink"
echo " for this release prior to executing this script."
echo " If this is not the case, stop here."
echo "**********************************************************"

read -p "Proceed? (y/n)"

if [ "$REPLY" != "y" ]; then
	echo "Aborting..."
	exit 0
fi

#if the target directory already exists, warn the user and
#give the opportunity to quit.

if [ -d "$target_dir" ]; then
	echo ""
	echo ""
	echo "************************* WARNING *************************"
	echo " The directory $target_dir already exists."
	echo "***********************************************************"
	echo ""
	read -p "Overwrite? (y/n)"

	if [ "$REPLY" != "y" ]; then
		echo "Aborting..."
		exit 0
	fi
	
	#delete the old target directory
	rm -rf "$target_dir"
fi


#create the target directory
mkdir "$target_dir"

#uncompress the template application to the target directory
tar xf "_build_app/AG2AppTemplate.tgz" -C "$target_dir"

#copy the image directory
cp -a ../target/image "$target_dir/AutoGrader2.app/Contents/macOS"

#compress to a tgz
cd "$target_dir"
tar cz "AutoGrader2.app" > "AutoGrader2-$version.tgz"

#remove the app
rm -rf AutoGrader2.app

echo "Done."


