#!/bin/bash

TABLE_NAME=Accounts
KEY=userEmail

aws dynamodb scan --table-name $TABLE_NAME --attributes-to-get "userEmail" --query "Items[].userEmail.S" --output text --endpoint-url http://localhost:8000 | tr "\t" "\n" | xargs -t -I keyvalue aws dynamodb delete-item --table-name $TABLE_NAME --key '{"userEmail": {"S": "keyvalue"}}' --endpoint-url http://localhost:8000