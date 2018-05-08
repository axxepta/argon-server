package de.axxepta.spark;


import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.servlet.SparkApplication;

public class MySparkApplication implements SparkApplication {

    @Override
    public void init() {
    
        Spark.get("/spark/hello",
                  (Request request, Response response) -> {
                      return "Hello World!";
        });
    }
}