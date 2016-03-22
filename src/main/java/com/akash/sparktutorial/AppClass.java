/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.akash.sparktutorial;
import com.rabbitmq.client.AMQP.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import static spark.Spark.*;
/**
 *
 * @author Akash
 */
public class AppClass {
    public static void main(String[] args) throws Exception{
        port(3990);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        com.rabbitmq.client.Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        final String QUEUE_NAME = "hello";
        
        // test route for heroku
        get("/default/:name", (req, res)->{
            return "Hello "+req.params(":name")+" from heroku";
        });
        
        //sample route
        get("/hello/:name", (req, res) -> {
            channel.basicPublish("", QUEUE_NAME, null, "hello world".getBytes()); //test
            System.out.println("[x] Sent"); //test
            return "Hello:" + req.params(":name")+"\n New message publishes to RabbitMQ";
        });
        
        //route to take in the dashboard requets
        post("/request", (req, res) -> {
            String payload = null;
            if(req.contentType().equals("application/json")){
                //payload in proper format, send request as message to rabbit
                payload = req.body();
                channel.basicPublish("", QUEUE_NAME, null, payload.getBytes());
            }else{
                //payload in incorrect format, send response error
            }
            System.out.println(req.contentType()+"\n"+payload);
            return "hello";
        });
    }
}
