#Directories


#Files
ALL_FILES = $(wildcard *.java */*.java)
ALL_CLASSES = $(wildcard *.class */*.class)
ALL_CLASSES := $(subst /,\,$(ALL_CLASSES))

debug:
#@echo $(ALL_FILES)
	@echo $(ALL_CLASSES)

build:
	@echo Building...
	@javac -cp ".." $(ALL_FILES)

clean:
	@echo Cleaning...
	@del $(ALL_CLASSES)