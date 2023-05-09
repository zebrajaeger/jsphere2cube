package de.zebrajaeger.sphere2cube.psd;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import de.zebrajaeger.sphere2cube.image.ResourceSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * The pds resource section, divided in resource blocks
 *
 * @author Lars Brandt on 08.05.2016.
 */
public class ResourceSectionImpl implements ResourceSection {
    private final static Logger LOG = LoggerFactory.getLogger(ResourceSectionImpl.class);

    private final LinkedList<ResourceBlockImpl> resourceBlocks = new LinkedList<>();

    public ResourceSectionImpl(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        try {
            // minimum header size at least 10 bytes
            while (bb.remaining() >= 10) {
                LOG.debug("Read resourceblock. Available: {}", bb.remaining());
                final ResourceBlockImpl irb = new ResourceBlockImpl();
                irb.read(bb);
                resourceBlocks.add(irb);
            }
        } catch (IOException e) {
            LOG.error("Could not read ResourceBlock", e);
        }
    }

    public List<ResourceBlockImpl> getResourceBlocks() {
        return resourceBlocks;
    }

    @Deprecated
    public GPanoData getGPanoData() {
        for (final ResourceBlockImpl b : resourceBlocks) {
            if (b.getDecodedData() instanceof GPanoData) {
                return (GPanoData) b.getDecodedData();
            }
        }
        return null;
    }
}
