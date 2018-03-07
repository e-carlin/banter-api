#!/bin/bash

TABLE_NAME=Accounts
KEY=userId

aws dynamodb scan --table-name $TABLE_NAME --attributes-to-get "userId" --query "Items[].userId.S" --output text --endpoint-url http://localhost:8000 | tr "\t" "\n" | xargs -t -I keyvalue aws dynamodb delete-item --table-name $TABLE_NAME --key '{"userId": {"S": "keyvalue"}}' --endpoint-url http://localhost:8000