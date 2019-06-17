#up database
.PHONY: up
up:
	docker-compose up -d db
	./gradlew -p agi-example -Dspring.profiles.active=dev bootRun
