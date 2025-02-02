/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.grid.GridFlow;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.Grid.GridOrder;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

@Category(UnitTest.class)
public class GridUnitTest extends ExtendedITextTest {

    @Test
    public void getUniqueCellsTest() {
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_COLUMN_START, 2);
        twoRenderer.setProperty(Property.GRID_COLUMN_END, 4);
        Grid grid = Grid.Builder.forItems(Arrays.asList(
                new TextRenderer(new Text("One")),
                twoRenderer,
                new TextRenderer(new Text("Three")),
                new TextRenderer(new Text("Four"))
        )).columns(3).rows(3).flow(GridFlow.ROW).build();
        Assert.assertEquals(4, grid.getUniqueGridCells(Grid.GridOrder.ROW).size());
    }

    @Test
    public void getUniqueCellsInColumnAndRowTest() {
        IRenderer twoRenderer = new TextRenderer(new Text("Two"));
        twoRenderer.setProperty(Property.GRID_ROW_START, 2);
        twoRenderer.setProperty(Property.GRID_ROW_END, 4);
        Grid grid = Grid.Builder.forItems(Arrays.asList(
                new TextRenderer(new Text("One")),
                twoRenderer,
                new TextRenderer(new Text("Three")),
                new TextRenderer(new Text("Four"))
        )).columns(3).rows(3).flow(GridFlow.ROW).build();
        Assert.assertEquals(1, grid.getUniqueCellsInTrack(GridOrder.COLUMN, 1).size());
        Assert.assertEquals(3, grid.getUniqueCellsInTrack(GridOrder.ROW, 0).size());
    }

    @Test
    public void invalidColumnForGetColCellsTest() {
        Grid grid = new Grid(3, 3);
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, 4));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, -1));
        AssertUtil.doesNotThrow(() -> grid.getUniqueCellsInTrack(GridOrder.COLUMN, 2));
    }

    @Test
    public void invalidRowForGetRowCellsTest() {
        Grid grid = new Grid(3, 3);
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.ROW, 4));
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> grid.getUniqueCellsInTrack(GridOrder.ROW, -1));
        AssertUtil.doesNotThrow(() -> grid.getUniqueCellsInTrack(GridOrder.ROW, 2));
    }

    @Test
    public void sparsePackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 2);
        wideCell.setProperty(Property.GRID_COLUMN_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.ROW).build();
        Assert.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assert.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assert.assertEquals(wideCell, grid.getRows()[0][2].getValue());
        Assert.assertEquals(cell3, grid.getRows()[1][0].getValue());
        Assert.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assert.assertEquals(cell5, grid.getRows()[1][2].getValue());
        Assert.assertEquals(cell6, grid.getRows()[2][0].getValue());
    }

    @Test
    public void densePackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 2);
        wideCell.setProperty(Property.GRID_COLUMN_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.ROW_DENSE).build();
        Assert.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assert.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assert.assertEquals(wideCell, grid.getRows()[0][2].getValue());
        Assert.assertEquals(cell3, grid.getRows()[1][0].getValue());
        Assert.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assert.assertEquals(cell5, grid.getRows()[1][2].getValue());
        Assert.assertEquals(cell6, grid.getRows()[2][0].getValue());
    }

    @Test
    public void columnPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer cell2 = new TextRenderer(new Text("Two"));
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));
        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                cell2,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assert.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assert.assertEquals(cell2, grid.getRows()[1][0].getValue());
        Assert.assertEquals(cell3, grid.getRows()[2][0].getValue());
        Assert.assertEquals(cell4, grid.getRows()[0][1].getValue());
        Assert.assertEquals(cell5, grid.getRows()[1][1].getValue());
        Assert.assertEquals(cell6, grid.getRows()[2][1].getValue());
    }

    @Test
    public void columnWithFixedWideCellPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer wideCell = new TextRenderer(new Text("Two"));
        wideCell.setProperty(Property.GRID_COLUMN_START, 1);
        wideCell.setProperty(Property.GRID_COLUMN_END, 3);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                wideCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assert.assertEquals(wideCell, grid.getRows()[0][0].getValue());
        Assert.assertEquals(cell1, grid.getRows()[1][0].getValue());
        Assert.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assert.assertEquals(cell3, grid.getRows()[2][0].getValue());
        Assert.assertEquals(wideCell, grid.getRows()[0][1].getValue());
        Assert.assertEquals(cell5, grid.getRows()[2][1].getValue());
        Assert.assertEquals(cell6, grid.getRows()[0][2].getValue());
    }

    @Test
    public void columnWithFixedTallCellPackingTest() {
        IRenderer cell1 = new TextRenderer(new Text("One"));
        IRenderer tallCell = new TextRenderer(new Text("Two"));
        tallCell.setProperty(Property.GRID_ROW_START, 2);
        tallCell.setProperty(Property.GRID_ROW_END, 4);
        IRenderer cell3 = new TextRenderer(new Text("Three"));
        IRenderer cell4 = new TextRenderer(new Text("Four"));
        IRenderer cell5 = new TextRenderer(new Text("Five"));
        IRenderer cell6 = new TextRenderer(new Text("Six"));

        Grid grid = Grid.Builder.forItems(Arrays.asList(
                cell1,
                tallCell,
                cell3,
                cell4,
                cell5,
                cell6
        )).columns(3).rows(3).flow(GridFlow.COLUMN).build();
        Assert.assertEquals(cell1, grid.getRows()[0][0].getValue());
        Assert.assertEquals(tallCell, grid.getRows()[1][0].getValue());
        Assert.assertEquals(tallCell, grid.getRows()[2][0].getValue());
        Assert.assertEquals(cell3, grid.getRows()[0][1].getValue());
        Assert.assertEquals(cell4, grid.getRows()[1][1].getValue());
        Assert.assertEquals(cell5, grid.getRows()[2][1].getValue());
        Assert.assertEquals(cell6, grid.getRows()[0][2].getValue());
    }
}
