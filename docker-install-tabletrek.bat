docker pull postgres:latest
docker run --name tableTrekDB -p 5432:5432 -e POSTGRES_USER=JoJoRamen -e POSTGRES_PASSWORD=JoJoKaraMisso -e POSTGRES_DB=tabletrekdb -d postgres