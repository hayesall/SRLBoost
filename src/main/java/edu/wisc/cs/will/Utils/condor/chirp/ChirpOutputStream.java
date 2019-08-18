package edu.wisc.cs.will.Utils.condor.chirp;

import java.io.IOException;

/**
A ChirpOutputStream gives a sequential binary interface to a write-only
file. Users that require random-access I/O should see ChirpClient.
Users requiring a character-oriented interface to a Chirp file
should make use of an OutputStreamWriter like so:
<pre>
OutputStreamWriter in = new OutputStreamWriter(new ChirpOutputStream(filename));
</pre> 
*/

public class ChirpOutputStream extends java.io.OutputStream {
	private ChirpClient client;
	private int fd;

	private void init(String p, boolean append) throws IOException {
		client = new ChirpClient();
		if(append) {
			fd = client.open(p,"wac", 511);
		} else {
			fd = client.open(p,"wtc", 511);
		}
	}

	public ChirpOutputStream( String p ) throws IOException {
		init(p,false);
	}

	public ChirpOutputStream( String p, boolean append ) throws IOException {
		init(p,append);
	}

	public void write( byte [] buffer, int pos, int length ) throws IOException {
		while( length>0 ) {
			int actual = client.write(fd,buffer,pos,length);
			length -= actual;
			pos += actual;
		}
	}

	public void write( byte [] buffer ) throws IOException {
		write(buffer,0,buffer.length);
	}

	public void write( int b ) throws IOException {
		byte [] temp = new byte[1];
		temp[0] = (byte)b;
		write(temp,0,1);
	}

	public void close() throws IOException {
		client.close(fd);
	}

	public void flush() {}
}

