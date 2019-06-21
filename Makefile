version=0.1.0-SNAPSHOT

NAME=hermes
COMMIT=$(shell git rev-parse HEAD)
ARCH=$(shell dpkg --print-architecture)
ENV=

ifeq ($(ENV), prod)
	VERSION=$(shell git describe --tags | awk -F 'v' '{print $$2}')
	PACKAGE_CLOUD_SRC=
else
	VERSION=0
	PACKAGE_CLOUD_SRC=-dev
	ENV=dev
endif

test:
	./gradlew test

build:
	./gradlew bootRepackage

build4docker: clean build
	docker build -t button/hermes .

clean:
	./gradlew clean

dep-updates:
	gradle dependencyUpdates

server: clean
	./gradlew -Dspring.profiles.active=ideToLocalDocker bootRun

run: build
	java -jar build/libs/hermes.jar

run_docker: build
	java -Dspring.profiles.active=docker -jar build/libs/hermes-$(version).jar

# this target lists all the make targets in this Makefile
list:
	@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null \
		| awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' \
		| sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs
