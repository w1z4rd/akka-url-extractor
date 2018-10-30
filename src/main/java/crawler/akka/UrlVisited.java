package crawler.akka;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UrlVisited extends AbstractActor {

  private static final Object PRESET = new Object();

  private final Map<String, Object> visited = new HashMap<>(1000, 0.99F);

  public static class AddVisited {

    private final String url;

    public AddVisited(String url) {
      this.url = url;
    }
  }

  public static class IsNotVisited {

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      IsNotVisited that = (IsNotVisited) o;
      return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
      return Objects.hash(url);
    }

    private final String url;

    public IsNotVisited(String url) {
      this.url = url;
    }
  }

  public static class Visit {
    String url;

    public Visit(String url) {
      this.url = url;
    }

  }

  public static Props props() {
    return Props.create(UrlVisited.class);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(AddVisited.class, this::addUrl)
        .match(IsNotVisited.class, this::isNotVisited)
        .build();
  }

  private void isNotVisited(IsNotVisited message) {
    boolean notVisited = !visited.containsKey(message.url);
    if (notVisited) {
      visited.put(message.url, PRESET);
      getSender().tell(new Visit(message.url), getSelf());
    } else {
      getSender().tell(PoisonPill.getInstance(), getSelf());
    }
  }

  private void addUrl(AddVisited message) {
    visited.put(message.url, PRESET);
  }

}
