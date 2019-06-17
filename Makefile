#up database
.PHONY: up
up:
	docker-compose up -d db
	./gradlew -p agi-example bootRun
