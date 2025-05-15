Docker commmands:

    docker run -d --name oms-app --network my-network \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-db:3306/your_db \
    -e SPRING_DATASOURCE_USERNAME=user \
    -e SPRING_DATASOURCE_PASSWORD=password \
    order-management-system