package crawler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import crawler.akka.Coordinator;
import crawler.akka.UrlAggregator;
import crawler.akka.UrlVisited;
import crawler.util.HtmlParser;

public class AkkaApplication {

    public static void main(String[] args) {
        final HtmlParser htmlParser = new HtmlParser();
        final ActorSystem system = ActorSystem.create("crawler");
        final ActorRef visitedRef = system.actorOf(UrlVisited.props().withDispatcher("blocking-dispatcher"), "visited");
        final ActorRef aggregatorRef = system
                .actorOf(UrlAggregator.props().withDispatcher("blocking-dispatcher"), "aggregator");
        final ActorRef coordinator = system
                .actorOf(Coordinator.props(aggregatorRef, visitedRef, htmlParser).withDispatcher("blocking-dispatcher"),
                        "coordinator");

        final String url = "https://en.wikipedia.org/wiki/Europe";
        coordinator.tell(new Coordinator.EntryPoint(url), ActorRef.noSender());
    }

}
