package io.simple.nio;

import java.io.IOException;

import io.simple.nio.store.FileStore;
import io.simple.util.IoUtil;

public class Configuration {
	final static long MAX_STORE_SIZE = 1L << 33;
	
	private boolean daemon = false;
	
	private String name  = "Simple-nio";
	private String host  = "0.0.0.0";
	private int port     = 9696;
	private int backlog  = 1024;
	
	// resource limit
	private int maxConns = 10240, maxServerConns, maxClientConns;
	private int maxReadBuffers = 8, maxWriteBuffers = 64, writeSpinCount = 16;
	
	// timeout settings
	private long connectTimeout = 30000L, readTimeout = 30000L, writeTimeout = 60000L;
	
	private boolean autoRead     = true;
	private boolean bufferDirect = true;
	private int bufferSize       = BufferPool.DEFAULT_BUFFER_SIZE;
	private long poolSize, storeSize;
	private BufferPool bufferPool;
	private FileStore  bufferStore;
	
	private SessionInitializer serverInitializer;
	private SessionInitializer clientInitializer;
	
	private EventLoopListener eventLoopListener;
	
	{
		final long max = Runtime.getRuntime().maxMemory();
		poolSize  = max >> 1;
		storeSize = max << 1;
		if(storeSize > MAX_STORE_SIZE) {
			storeSize = MAX_STORE_SIZE;
		}
	}
	
	public Configuration() {
		
	}

	public String getName() {
		return name;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getBacklog() {
		return backlog;
	}
	
	public boolean isDaemon() {
		return daemon;
	}
	
	public int getMaxConns() {
		return maxConns;
	}
	
	public int getMaxClientConns() {
		return maxClientConns;
	}

	public int getMaxServerConns() {
		return maxServerConns;
	}
	
	public boolean isAutoRead() {
		return autoRead;
	}
	
	public boolean isBufferDirect() {
		return bufferDirect;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public long getPoolSize() {
		return poolSize;
	}
	
	public long getStoreSize() {
		return storeSize;
	}
	
	public BufferPool getBufferPool() {
		return bufferPool;
	}
	
	public FileStore getBufferStore() {
		return bufferStore;
	}
	
	public SessionInitializer getServerInitializer() {
		return serverInitializer;
	}

	
	public SessionInitializer getClientInitializer() {
		return clientInitializer;
	}
	
	public EventLoopListener getEventLoopListener(){
		return eventLoopListener;
	}
	
	/**
	 * <p>
	 * An input rate limit way for saving buffer memory consumption. 
	 * When beyond this, don't read bytes from channel until a buffer released.
	 * </p>
	 * 
	 * <p>
	 * <b>Note</b>: {@link #getMaxReadBuffers()} x {@link #getBufferSize()} should
	 * be bigger than max packet size in user protocol.
	 * </p>
	 * 
	 * @return max read buffer number
	 */
	public int getMaxReadBuffers() {
		return maxReadBuffers;
	}

	/**
	 * An output rate limit way for saving buffer memory consumption. 
	 * When beyond this, cache bytes into disc.
	 * 
	 * @return max write buffer number
	 */
	public int getMaxWriteBuffers() {
		return maxWriteBuffers;
	}
	
	/**
	 * The maximum loop count for a write operation until channel 
	 *returns a non-zero value.
	 * 
	 * @return write spin count
	 */
	public int getWriteSpinCount(){
		return writeSpinCount;
	}
	
	public long getConnectTimeout() {
		return connectTimeout;
	}
	
	public long getReadTimeout() {
		return readTimeout;
	}

	public long getWriteTimeout() {
		return writeTimeout;
	}
	
	public final static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Configuration config = new Configuration();
		
		public Builder() {
			
		}
		
		public Builder setName(String name) {
			config.name = name;
			return this;
		}
		
		public Builder setDaemon(boolean daemon) {
			config.daemon = daemon;
			return this;
		}
		
		public Builder setHost(String host) {
			config.host = host;
			return this;
		}
		
		public Builder setPort(int port) {
			config.port = port;
			return this;
		}
		
		public Builder setBacklog(int backlog) {
			config.backlog = backlog;
			return this;
		}
		
		/**
		 * @param maxConns
		 * @return the default max connections for server or client
		 */
		public Builder setMaxConns(int maxConns) {
			config.maxConns = maxConns;
			return this;
		}
		
		public Builder setMaxServerConns(int maxServerConns) {
			config.maxServerConns = maxServerConns;
			return this;
		}
		
		public Builder setMaxClientConns(int maxClientConns) {
			config.maxClientConns = maxClientConns;
			return this;
		}
		
		public Builder setAutoRead(boolean autoRead) {
			config.autoRead = autoRead;
			return this;
		}
		
		public Builder setBufferDirect(boolean bufferDirect) {
			config.bufferDirect = bufferDirect;
			return this;
		}
		
		public Builder setBufferSize(int bufferSize) {
			config.bufferSize = bufferSize;
			return this;
		}
		
		public Builder setPoolSize(long poolSize) {
			config.poolSize = poolSize;
			return this;
		}
		
		public Builder setStoreSize(long storeSize) {
			config.storeSize = storeSize;
			return this;
		}
		
		public Builder setMaxReadBuffers(int maxReadBuffers) {
			config.maxReadBuffers  = maxReadBuffers;
			return this;
		}
		
		public Builder setMaxWriteBuffers(int maxWriteBuffers) {
			config.maxWriteBuffers = maxWriteBuffers;
			return this;
		}
		
		public Builder setWriteSpinCount(int writeSpinCount){
			config.writeSpinCount = writeSpinCount;
			return this;
		}
		
		public Builder setServerInitializer(SessionInitializer serverInitializer) {
			config.serverInitializer = serverInitializer;
			return this;
		}
		
		public Builder setClientInitializer(SessionInitializer clientInitializer) {
			config.clientInitializer = clientInitializer;
			return this;
		}
		
		public Builder setEventLoopListener(EventLoopListener eventLoopListener){
			config.eventLoopListener = eventLoopListener;
			return this;
		}
		
		public Builder setConnectTimeout(long connectTimeout) {
			config.connectTimeout = connectTimeout;
			return this;
		}
		
		public Builder setReadTimeout(long readTimeout) {
			config.readTimeout = readTimeout;
			return this;
		}
		
		public Builder setWriteTimeout(long writeTimeout) {
			config.writeTimeout = writeTimeout;
			return this;
		}
		
		public Configuration build() {
			final Configuration config  = this.config;
			
			if(config.serverInitializer == null && config.clientInitializer == null) {
				throw new IllegalArgumentException("No server or client session initializer");
			}
			
			final int maxConns = config.maxConns;
			if(maxConns < 1) {
				throw new IllegalArgumentException("maxConns must bigger than 0: "+config.maxConns);
			}
			if(config.maxServerConns <= 0){
				config.maxServerConns = maxConns;
			}
			if(config.maxClientConns <= 0){
				config.maxClientConns = maxConns;
			}
			
			if(config.maxReadBuffers  < 1) {
				throw new IllegalArgumentException("maxReadBuffers must bigger  than 0: "+config.maxReadBuffers);
			}
			if(config.maxWriteBuffers < 1) {
				throw new IllegalArgumentException("maxWriteBuffers must bigger than 0: "+config.maxWriteBuffers);
			}
			if(config.writeSpinCount  < 1) {
				throw new IllegalArgumentException("writeSpinCount must bigger than 0: "+config.writeSpinCount);
			}
			
			final int bufferSize = config.bufferSize;
			final long poolSize  = config.poolSize;
			final long storeSize = config.storeSize;
			if(storeSize > MAX_STORE_SIZE) {
				throw new IllegalArgumentException("storeSize can't bigger than "+MAX_STORE_SIZE+": "+storeSize);
			}
			boolean failed = true;
			try {
				try {
					config.bufferStore   = FileStore.open("BufferStore", storeSize, bufferSize);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
				if(config.isBufferDirect()) {
					config.bufferPool = new ArrayBufferPool(poolSize, bufferSize);
				}else {
					config.bufferPool = new SimpleBufferPool(poolSize, bufferSize);
				}
				
				if(config.eventLoopListener == null){
					config.eventLoopListener = EventLoopListener.NOOP;
				}
				
				// Create a new config for the building safe
				this.config = new Configuration();
				setServerInitializer(config.serverInitializer);
				setClientInitializer(config.clientInitializer);
				setEventLoopListener(config.eventLoopListener);
				
				failed = false;
				return config;
			}finally {
				if(failed) {
					IoUtil.close(config.bufferStore);
					config.bufferStore = null;
					IoUtil.close(config.bufferPool);
					config.bufferPool  = null;
				}
			}
		}
		
		public EventLoop boot() {
			return new EventLoop(build());
		}
		
	}

}
