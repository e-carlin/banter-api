aws dynamodb create-table --table-name Customer --attribute-definitions AttributeName=Id,AttributeType=S --key-schema AttributeName=Id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000
aws dynamodb create-table --table-name InstitutionTokens --attribute-definitions AttributeName=itemId,AttributeType=S --key-schema AttributeName=itemId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000


Validation handling
http://localhost:63342/api/markdown-preview/styles/default.css?_ijt=g2m1t7mhmn3ir3lk4ktai0ofgk

