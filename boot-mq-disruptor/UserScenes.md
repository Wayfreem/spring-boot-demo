## 使用 disruptor 的场景

### 事件处理

Disruptor 适用于事件驱动架构，实现高效的事件处理。

```java
public class EventData {
    private String data;
    public void setData(String data) { this.data = data; }
    public String getData() { return data; }
}

public class EventFactory implements EventFactory<EventData> {
    @Override
    public EventData newInstance() { return new EventData(); }
}

public class EventHandler implements EventHandler<EventData> {
    @Override
    public void onEvent(EventData event, long sequence, boolean endOfBatch) {
        System.out.println("Processing event: " + event.getData());
    }
}

public class EventProcessingSystem {
    public static void main(String[] args) {
        Disruptor<EventData> disruptor = new Disruptor<>(
                new EventFactory(),
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new SleepingWaitStrategy()
        );
        
        disruptor.handleEventsWith(new EventHandler());
        disruptor.start();
        
        RingBuffer<EventData> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((event, sequence) -> event.setData("Sample Event"));
    }
}
```

### 日志记录

Disruptor 适合用作高性能日志队列，避免传统阻塞队列的性能瓶颈。

```java
public class LogEvent {
    private String message;
    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }
}

public class LogEventFactory implements EventFactory<LogEvent> {
    @Override
    public LogEvent newInstance() { return new LogEvent(); }
}

public class LogEventHandler implements EventHandler<LogEvent> {
    @Override
    public void onEvent(LogEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Log: " + event.getMessage());
    }
}

public class DisruptorLogSystem {
    public static void main(String[] args) {
        Disruptor<LogEvent> disruptor = new Disruptor<>(
                new LogEventFactory(),
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new SleepingWaitStrategy()
        );
        
        disruptor.handleEventsWith(new LogEventHandler());
        disruptor.start();
        
        RingBuffer<LogEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((event, sequence) -> event.setMessage("Test Log"));
    }
}
```

### 消息传递

Disruptor 适用于高吞吐量的消息传递系统，例如消息队列，以及实时消息等。

```java
public class MessageEvent {
    private String message;
    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }
}

public class MessageEventHandler implements EventHandler<MessageEvent> {
    @Override
    public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Received message: " + event.getMessage());
    }
}

public class DisruptorMessageQueue {
    public static void main(String[] args) {
        Disruptor<MessageEvent> disruptor = new Disruptor<>(
                MessageEvent::new,
                1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );
        
        disruptor.handleEventsWith(new MessageEventHandler());
        disruptor.start();
        
        RingBuffer<MessageEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent((event, sequence) -> event.setMessage("Hello Disruptor"));
    }
}
```

### 实时数据分析

Disruptor 可用于高并发环境下的实时数据流处理。

```java
public class DataEvent {
    private double value;
    public void setValue(double value) { this.value = value; }
}

public class DataAnalyzer implements EventHandler<DataEvent> {
    @Override
    public void onEvent(DataEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Analyzing data: " + event.value);
    }
}
```

### 并发任务调度

在高并发环境下，使用 Disruptor 可以构建高效的异步任务调度系统。

```java
public class TaskEvent {
    private Runnable task;
    public void setTask(Runnable task) { this.task = task; }
}

public class TaskHandler implements EventHandler<TaskEvent> {
    @Override
    public void onEvent(TaskEvent event, long sequence, boolean endOfBatch) {
        event.task.run();
    }
}
```