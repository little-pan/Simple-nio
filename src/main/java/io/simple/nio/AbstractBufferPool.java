package io.simple.nio;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.simple.util.MathUtil;

public abstract class AbstractBufferPool implements BufferPool {
	
	final static Logger log = LoggerFactory.getLogger(AbstractBufferPool.class);
	
	protected final long poolSize;
	protected final int bufferSize;
	protected final int bufferSizeShift;
	
	protected long pooledSize;
	protected long curSize;
	
	private boolean closed;
	
	protected AbstractBufferPool(long poolSize) {
		this(poolSize, DEFAULT_BUFFER_SIZE);
	}
	
	protected AbstractBufferPool(long poolSize, int bufferSize) {
		this.poolSize   = poolSize;
		this.bufferSize = bufferSize;
		if(poolSize   <= 0L) {
			throw new IllegalArgumentException("poolSize must bigger than 0: " + poolSize);
		}
		if(bufferSize <= 0) {
			throw new IllegalArgumentException("bufferSize must bigger than 0: " + bufferSize);
		}
		this.bufferSizeShift = MathUtil.bitShift(bufferSize);
		log.info("{}: poolSize = {}, bufferSize = {}, bufferSizeShift = {}", 
				this, poolSize, bufferSize, bufferSizeShift);
	}
	
	@Override
	public Buffer allocate() throws BufferAllocateException {
		checkNotClosed();
		
		if(curSize + bufferSize > poolSize) {
			throw new BufferAllocateException("Exceeds pool size limit");
		}
		final ByteBuffer buf = doAllocate();
		final Buffer buffer = new Buffer(this, buf);
		buffer.onAlloc();
		curSize += bufferSize;
		log.debug("{}: Allocate a buffer from VM - {}", this, buffer);
		return buffer;
	}

	@Override
	public void release(Buffer buffer) {
		checkNotClosed();
		
		if(buffer.bufferPool() == this) {
			buffer.onRelease();
			doRelease(buffer);
			curSize -= bufferSize;
			return;
		}
		log.warn("{}: buffer not allocated from this pool - {}", this, buffer);
	}
	
	protected abstract ByteBuffer doAllocate();
	
	protected void doRelease(Buffer buffer) {
		log.debug("{}: Release a buffer into VM - {}", this, buffer);
	}
	
	protected void checkNotClosed(){
		if(closed){
			throw new IllegalStateException(this+" has closed");
		}
	}

	@Override
	public long pooledSize() {
		return pooledSize;
	}
	
	@Override
	public long available() {
		return (poolSize - curSize);
	}
	
	@Override
	public int bufferSize() {
		return bufferSize;
	}
	
	@Override
	public int bufferSizeShift() {
		return bufferSizeShift;
	}
	
	@Override
	public boolean isOpen(){
		return !closed;
	}
	
	@Override
	public void close(){
		closed = true;
	}
	
	@Override
	public String toString() {
		return "BufferPool";
	}
	
}
