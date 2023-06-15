#Directories
MAIN_DIR = connectx

#Files
ALL_FILES = $(wildcard $(MAIN_DIR)/*.java $(MAIN_DIR)/*/*.java)
ALL_CLASSES = $(wildcard $(MAIN_DIR)/*.class $(MAIN_DIR)/*/*.class)
ALL_CLASSES := $(subst /,\,$(ALL_CLASSES))

debug:
#@echo $(ALL_FILES)
	@echo $(ALL_CLASSES)

main: build

build:
	@echo Building...
	@javac -cp ".." $(ALL_FILES)

clean:
	@echo Cleaning...
	@del $(ALL_CLASSES)

#run a debug game and take parameters from command line
run: build
	@echo Running MyPlayer vs Human, 4 6 4
	@java connectx.CXGame 4 6 4 connectx.Player.MyPlayer