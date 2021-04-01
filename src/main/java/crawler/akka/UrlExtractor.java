package crawler.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import crawler.akka.UrlAggregator.AddUrl;
import crawler.akka.UrlVisited.IsNotVisited;
import crawler.util.HtmlParser;

public class UrlExtractor extends AbstractActor {

  private final ActorRef visitedActor;
  private final ActorRef aggregatorActor;
  private final HtmlParser htmlParser;

  public UrlExtractor(ActorRef visitedActor, ActorRef aggregatorActor, HtmlParser htmlParser) {
    this.visitedActor = visitedActor;
    this.aggregatorActor = aggregatorActor;
    this.htmlParser = htmlParser;
  }

  public static class ParseUrl {

    private final String url;

    public ParseUrl(String url) {
      this.url = url;
    }
  }

  public static Props props(ActorRef visitedActor, ActorRef aggregatorActor, HtmlParser htmlParser) {
    return Props.create(UrlExtractor.class, visitedActor, aggregatorActor, htmlParser);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(ParseUrl.class, this::parseUrl)
        .match(UrlVisited.Visit.class, this::parse)
        .build();
  }

  private void parse(final UrlVisited.Visit message) {
      htmlParser.getUrls(message.url)
          .parallelStream()
          .forEach(url -> aggregatorActor.tell(new AddUrl(url), getSelf()));
    getSelf().tell(PoisonPill.getInstance(), getSelf());
  }


  private void parseUrl(final ParseUrl message) {
    visitedActor.tell(new IsNotVisited(message.url), self());
  }

}
