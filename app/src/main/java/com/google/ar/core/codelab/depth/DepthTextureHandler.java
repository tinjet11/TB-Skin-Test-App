// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.ar.core.codelab.depth;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES30.GL_LINEAR;
import static android.opengl.GLES30.GL_RG;
import static android.opengl.GLES30.GL_RG8;

import android.media.Image;

import com.google.ar.core.Frame;
import com.google.ar.core.exceptions.NotYetAvailableException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Handle RG8 GPU texture containing a DEPTH16 depth image. */
public final class DepthTextureHandler {

  private int depthTextureId = -1;
  private int depthTextureWidth = -1;
  private int depthTextureHeight = -1;

  private Image depthImage;

  private int depthValue;



  /**
   * Creates and initializes the depth texture. This method needs to be called on a
   * thread with a EGL context attached.
   */
  public void createOnGlThread() {
    int[] textureId = new int[1];
    glGenTextures(1, textureId, 0);
    depthTextureId = textureId[0];
    glBindTexture(GL_TEXTURE_2D, depthTextureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  }

  /**
   * Updates the depth texture with the content from acquireDepthImage().
   * This method needs to be called on a thread with a EGL context attached.
   */
  public void update(final Frame frame) {
    try {
      depthImage = frame.acquireDepthImage16Bits();
      depthTextureWidth = depthImage.getWidth();
      depthTextureHeight = depthImage.getHeight();
      depthValue = getMillimetersDepth(depthImage,1,1);
      glBindTexture(GL_TEXTURE_2D, depthTextureId);
      glTexImage2D(
          GL_TEXTURE_2D,
          0,
          GL_RG8,
          depthTextureWidth,
          depthTextureHeight,
          0,
          GL_RG,
          GL_UNSIGNED_BYTE,
          depthImage.getPlanes()[0].getBuffer());
      depthImage.close();

    } catch (NotYetAvailableException e) {
      // This normally means that depth data is not available yet.
    }
  }

  /** Obtain the depth in millimeters for depthImage at coordinates (x, y). */
  public static int getMillimetersDepth(Image depthImage, int x, int y) {
    // The depth image has a single plane, which stores depth for each
    // pixel as 16-bit unsigned integers.
    Image.Plane plane = depthImage.getPlanes()[0];
    /** pixel stride = the number of bytes between each pixel in a row. (an image => 8 bits per pixel and has a pixel stride of 4 = each row of pixels will take up to 32 byte
     *  row stride = the number of bytes between the start of each row of pixels. (0->n)
     *  byteIndex = calculate the byte offset of a pixel at a given (x, y) coordinate.
     *  x * plane.getPixelStride() = calculate the byte offset of a pixel at a given (x, y) coordinate.
     *  y * plane.getRowStride() =  find the byte offset of the current row.
     * */
    int byteIndex = x * plane.getPixelStride() + y * plane.getRowStride();
    ByteBuffer buffer = plane.getBuffer().order(ByteOrder.nativeOrder());    // the order of the bytes is in short value
    return buffer.getShort(byteIndex);
  }
  /*
    Byte index | Pixel value
     ----------|------------
    0          | Pixel 0
    2          | Pixel 1
    4          | Pixel 2
    ...        | ...
    (x * pixelStride) | Pixel at coordinates (x, y)
   */

  /*
  public static Pair<Integer, Integer> getDepthCoordinates(Frame frame, Image depthImage) {
    float[] cpuCoordinates = new float[] {0.5f, 0.5f};
    float[] textureCoordinates = new float[2];

    frame.transformCoordinates2d(
            Coordinates2d.IMAGE_PIXELS,
            cpuCoordinates,
            Coordinates2d.TEXTURE_NORMALIZED,
            textureCoordinates);
    if (textureCoordinates[0] < 0 || textureCoordinates[1] < 0) {
      // There are no valid depth coordinates, because the coordinates in the CPU image are in the
      // cropped area of the depth image.
      return null;
    }
    return new Pair<>(
            (int) (textureCoordinates[0] * depthImage.getWidth()),
            (int) (textureCoordinates[1] * depthImage.getHeight()));
  }

   */


  public int getDepthTexture() {
    return depthTextureId;
  }

  public int getDepthWidth() {
    return depthTextureWidth;
  }

  public int getDepthHeight() {
    return depthTextureHeight;
  }

  public int getDepthValue() {return depthValue;}

  //public Image getDepthImage() {return depthImage;}


}
