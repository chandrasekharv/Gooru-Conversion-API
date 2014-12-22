package org.ednovo.gooru.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumer implements Runnable {

	private KafkaProperties kafkaProperties;

	private static ConsumerConnector consumer;

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	private static KafkaStream m_stream;

	private String topic;

	private static final String KAFKA_CONSUMER_PORT = "2181";

	private String zkConsumerConnectValue;

	public KafkaConsumer() {

	}

	public KafkaConsumer(String topic, String ip) {
		try {
			this.setTopic(topic);
			this.setZkConsumerConnectValue(ip + ":" + KAFKA_CONSUMER_PORT);
			consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Serialization failed" + e);
		}
	}

	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put(KafkaProperties.ZK_CONSUMER_CONNECT, this.getZkConsumerConnectValue());
		props.put(KafkaProperties.ZK_CONSUMER_GROUP, this.getTopic());
		props.put(KafkaProperties.ZK_SESSION_TIME_OUT_MS, KafkaProperties.ZK_SESSION_TIME_OUT_MS_VALUE);
		props.put(KafkaProperties.ZK_SYNCTIME_MS, KafkaProperties.ZK_SYNCTIME_MS_VALUE);
		props.put(KafkaProperties.AUTOCOMMIT_INTERVAL_MS, KafkaProperties.AUTOCOMMIT_INTERVAL_MS_VALUE);
		props.put(KafkaProperties.FETCH_SIZE, KafkaProperties.FETCH_SIZE_VALUE);
		props.put(KafkaProperties.AUTO_OFFSET_RESET, KafkaProperties.AUTO_OFFSET_RESET_VALUE);
		return new ConsumerConfig(props);

	}

	@Override
	public void run() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put(getTopic(), 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> listOfTopicsStreams = consumer.createMessageStreams(map);
		List<KafkaStream<byte[], byte[]>> listOfStream = listOfTopicsStreams.get(getTopic());
		m_stream = listOfStream.get(0);
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();

		while (it.hasNext()) {
			String message = new String(it.next().message());
			System.out.print(message);
		}

	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getZkConsumerConnectValue() {
		return zkConsumerConnectValue;
	}

	public void setZkConsumerConnectValue(String zkConsumerConnectValue) {
		this.zkConsumerConnectValue = zkConsumerConnectValue;
	}

}
