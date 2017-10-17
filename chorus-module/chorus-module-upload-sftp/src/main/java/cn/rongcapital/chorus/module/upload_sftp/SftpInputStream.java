/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.rongcapital.chorus.module.upload_sftp;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.sftp.SftpFileObject;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.fs.FSInputStream;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SftpInputStream extends FSInputStream {

    private final FileSystem.Statistics stats;

    private final CountingInputStream countingInputStream;

    public SftpInputStream(SftpFileObject sftpFileObject) throws FileSystemException {
        if (!sftpFileObject.exists()) {
            throw new IllegalArgumentException("File does not exists!");
        }
        countingInputStream = new CountingInputStream(sftpFileObject.getInputStream());
        stats = null;
    }

    @Override
    public long getPos() throws IOException {
        return countingInputStream.getCount() - 1;
    }

    @Override
    public synchronized int read() throws IOException {
        int byteRead = countingInputStream.read();
        if (stats != null && byteRead >= 0) {
            stats.incrementBytesRead(1);
        }
        return byteRead;
    }

    @Override
    public synchronized int read(byte buf[], int off, int len) throws IOException {
        return countingInputStream.read(buf, off, len);
    }

    @Override
    public synchronized void close() throws IOException {
        countingInputStream.close();
    }

    // We don't support seek.
    @Override
    public void seek(long pos) throws IOException {
        throw new IOException("Seek not supported");
    }

    @Override
    public boolean seekToNewSource(long targetPos) throws IOException {
        throw new IOException("Seek not supported");
    }

    // Not supported.
    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readLimit) {
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("Mark not supported");
    }

}
