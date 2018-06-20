package io.simple.nio;

import java.io.Closeable;

/**
 * The event handler context.
 * 
 * @author little-pan
 * @since 2018-06-20
 *
 */
public class HandlerContext implements Closeable {
	
	protected HandlerContext next;
	protected HandlerContext prev;
	
	final EventHandler handler;
	final Session session;
	
	public HandlerContext(Session session, EventHandler handler){
		this.session = session;
		this.handler = handler;
	}
	
	public Session session() {
		return session;
	}
	
	public EventHandler handler(){
		return handler;
	}
	
	public Buffer alloc()throws BufferAllocateException{
		return session.alloc();
	}
	
	public HandlerContext enableRead() {
		session.enableRead();
		return this;
	}
	
	public HandlerContext enableWrite() {
		session.enableWrite();
		return this;
	}
	
	public HandlerContext disableRead() {
		session.disableRead();
		return this;
	}
	
	public HandlerContext disableWrite() {
		session.disableWrite();
		return this;
	}
	
	public HandlerContext flush() {
		session.flush();
		return this;
	}
	
	public void close() {
		session.close();
	}

	public void fireConnected(){
		if(next != null){
			next.handler.onConnected(next);
		}
	}
	
	public void fireRead(Object in){
		if(next != null){
			next.handler.onRead(next, in);
		}
	}
	
	public void fireWrite(Object out){
		if(prev != null){
			prev.handler.onWrite(prev, out);
		}
	}
	
	public void fireFlushed(){
		if(next != null){
			next.handler.onFlushed(next);
		}
	}
	
	public void fireCause(Throwable cause){
		if(next != null){
			next.handler.onCause(next, cause);
		}
	}

}
