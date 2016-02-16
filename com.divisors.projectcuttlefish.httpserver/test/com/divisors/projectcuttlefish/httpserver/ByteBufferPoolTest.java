package com.divisors.projectcuttlefish.httpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.divisors.projectcuttlefish.httpserver.util.ByteBufferPool;
import com.divisors.projectcuttlefish.httpserver.util.ByteBufferPool.ByteBufferWrapper;


public class ByteBufferPoolTest {
	
	@Test
	public void test() {
		ByteBufferPool pool = ByteBufferPool.exponential(64, true, 5000);
		ByteBufferWrapper a = pool.get(593);
		assertNotNull(a);
		assertTrue(a.get().capacity() >= 593);
		ByteBufferWrapper b = pool.get(1);
		assertNotNull(b);
		assertTrue(b.get().capacity() >= 1);
		pool.recycle(a);
		ByteBufferWrapper c = pool.get(593);
		assertEquals(a, c);
		pool.get(594);
	}
	
}
