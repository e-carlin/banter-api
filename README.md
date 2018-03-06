aws dynamodb create-table --table-name InstitutionTokens --attribute-definitions AttributeName=itemId,AttributeType=S --key-schema AttributeName=itemId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000

aws dynamodb create-table --table-name Accounts --attribute-definitions AttributeName=userEmail,AttributeType=S --key-schema AttributeName=userEmail,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 --endpoint-url http://localhost:8000

Start DB:
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb

Validation handling
http://www.naturalprogrammer.com/spring-framework-rest-api-validation/


DynamoDB local viewer
https://github.com/aaronshaf/dynamodb-admin