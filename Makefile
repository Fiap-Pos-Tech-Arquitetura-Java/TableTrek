build:
	mvn compile

unit-test:
	mvn test

integration-test:
	mvn test -P integration-test

system-test:
	mvn test -P system-test

test : unit-test integration-test

package:
	mvn package

docker-build: package
	docker build -t table-trek-docker-image -f ./Dockerfile .

docker-start:
	docker compose -f docker-compose.yaml up -d