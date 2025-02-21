/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package com.pentaho.big.data.bundles.impl.shim.hdfs;

import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.hadoop.shim.api.core.ShimIdentifierInterface;
import org.pentaho.hadoop.shim.api.hdfs.HadoopFileSystem;
import org.pentaho.hadoop.shim.api.internal.Configuration;
import org.pentaho.hadoop.shim.api.cluster.NamedCluster;
import org.pentaho.hadoop.shim.api.internal.ShimIdentifier;
import org.pentaho.hadoop.shim.api.internal.fs.FileSystem;
import org.pentaho.hadoop.shim.spi.HadoopShim;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by bryan on 8/3/15.
 */
public class HadoopFileSystemFactoryImplTest {
  private boolean isActiveConfiguration;
  private HadoopFileSystemFactoryImpl hadoopFileSystemFactory;
  private NamedCluster namedCluster;
  private String identifier;
  private HadoopShim hadoopShim;
  private Configuration configuration;
  private FileSystem fileSystem;
  private ShimIdentifierInterface shimIdentifierInterface;

  @Before
  public void setup() throws IOException {
    namedCluster = mock( NamedCluster.class );
    isActiveConfiguration = true;
    hadoopShim = mock( HadoopShim.class );
    configuration = mock( Configuration.class );
    when( hadoopShim.createConfiguration( namedCluster ) ).thenReturn( configuration );
    fileSystem = mock( FileSystem.class );
    when( fileSystem.getDelegate() ).thenReturn( new DistributedFileSystem() );
    when( hadoopShim.getFileSystem( configuration ) ).thenReturn( fileSystem );
    identifier = "testId";
    shimIdentifierInterface = mock( ShimIdentifierInterface.class );
    when( shimIdentifierInterface.getId() ).thenReturn( identifier );
    hadoopFileSystemFactory =
      new HadoopFileSystemFactoryImpl( isActiveConfiguration, hadoopShim, "hdfs", shimIdentifierInterface );
  }

  @Test
  public void testCanHandleActiveConfig() {
    when( namedCluster.getShimIdentifier() ).thenReturn( identifier );
    assertTrue( hadoopFileSystemFactory.canHandle( namedCluster ) );
  }

  @Test
  public void testCreateMapr() throws IOException {
    when( namedCluster.isMapr() ).thenReturn( true );
    HadoopFileSystem hadoopFileSystem = hadoopFileSystemFactory.create( namedCluster );
    assertNotNull( hadoopFileSystem );
  }

  @Test
  public void testCreateNotMaprNoPort() throws IOException {
    String testHost = "testHost";
    when( namedCluster.isMapr() ).thenReturn( false );
    when( namedCluster.getHdfsHost() ).thenReturn( testHost );
    HadoopFileSystem hadoopFileSystem = hadoopFileSystemFactory.create( namedCluster );
    assertNotNull( hadoopFileSystem );
  }

  @Test
  public void testCreateNotMaprPort() throws IOException {
    String testHost = "testHost";
    String testPort = "testPort";
    when( namedCluster.isMapr() ).thenReturn( false );
    when( namedCluster.getHdfsHost() ).thenReturn( testHost );
    when( namedCluster.getHdfsPort() ).thenReturn( testPort );
    HadoopFileSystem hadoopFileSystem = hadoopFileSystemFactory.create( namedCluster );
    assertNotNull( hadoopFileSystem );
  }

  @Test( expected = IOException.class )
  public void testLocalFileSystem() throws IOException {
    when( namedCluster.isMapr() ).thenReturn( true );
    when( fileSystem.getDelegate() ).thenReturn( new LocalFileSystem() );
    hadoopFileSystemFactory.create( namedCluster );
  }
}

