/*
 * Copyright (c) 2018 Helmut Neemann.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.hdl.model2;

import de.neemann.digital.core.NodeException;
import de.neemann.digital.draw.elements.PinException;
import de.neemann.digital.draw.library.ElementNotFoundException;
import de.neemann.digital.hdl.model2.clock.ClockIntegratorGeneric;
import de.neemann.digital.hdl.model2.clock.HDLClockIntegrator;
import de.neemann.digital.hdl.printer.CodePrinterStr;
import de.neemann.digital.integration.ToBreakRunner;
import junit.framework.TestCase;

import java.io.IOException;

public class HDLModelTest extends TestCase {

    private HDLCircuit getCircuit(String filename, HDLClockIntegrator ci) throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        ToBreakRunner br = new ToBreakRunner(filename);
        return new HDLCircuit(br.getCircuit(), "main", new HDLModel(br.getLibrary()), ci);
    }

    public void testSimple() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/comb.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:1 defines (A->3), B:1 defines (B->2), C:1 defines (C->2))\n" +
                "  out(X:1 reads (X->1), Y:1 reads (Y_temp->2), Z:1 reads (Z_temp->2), Aident:1 reads (A->3))\n" +
                "  sig(Y_temp->2, s0->1, Z_temp->2, s1->1)\n" +
                "\n" +
                "  node Const\n" +
                "    in()\n" +
                "    out(out:1 defines (s1->1))\n" +
                "    s1->1 := 1:1\n" +
                "  node Not\n" +
                "    in(in:1 reads (A->3))\n" +
                "    out(out:1 defines (Z_temp->2))\n" +
                "    Z_temp->2 := NOT A\n" +
                "  node merged expression\n" +
                "    in(In_1:1 reads (B->2), in:1 reads (C->2))\n" +
                "    out(out:1 defines (Y_temp->2))\n" +
                "    Y_temp->2 := (B OR NOT C)\n" +
                "  node merged expression\n" +
                "    in(In_5:1 reads (Y_temp->2), In_1:1 reads (A->3), In_2:1 reads (C->2), In_1:1 reads (Z_temp->2), In_1:1 reads (B->2))\n" +
                "    out(out:1 defines (s0->1))\n" +
                "    s0->1 := ((A OR C) AND (Z_temp OR C) AND 1:1 AND NOT (B OR C) AND Y_temp)\n" +
                "  node D_FF\n" +
                "    in(D:1 reads (s0->1), C:1 reads (s1->1))\n" +
                "    out(Q:1 defines (X->1), ~Q:1 is not used)\n" +
                "\n" +
                "  Y:1 reads (Y_temp->2) := Y_temp->2\n" +
                "  Z:1 reads (Z_temp->2) := Z_temp->2\n" +
                "  Aident:1 reads (A->3) := A->3\n" +
                "end circuit main\n", cp.toString());
    }

    public void testSimple2() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/comb2.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:1 defines (A->1), B:1 defines (B->1), C:1 defines (C->1))\n" +
                "  out(Y:1 reads (Y->1))\n" +
                "  sig()\n" +
                "\n" +
                "  node merged expression\n" +
                "    in(In_2:1 reads (C->1), In_1:1 reads (A->1), In_2:1 reads (B->1))\n" +
                "    out(out:1 defines (Y->1))\n" +
                "    Y->1 := ((A AND B) OR C)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testInputInvert() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/inputInvert.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:1 defines (A->1), B:1 defines (B->1), C:1 defines (C->1))\n" +
                "  out(Y:1 reads (Y->1))\n" +
                "  sig()\n" +
                "\n" +
                "  node merged expression\n" +
                "    in(In_2:1 reads (B->1), In_3:1 reads (C->1), In_1:1 reads (A->1))\n" +
                "    out(out:1 defines (Y->1))\n" +
                "    Y->1 := ((A AND NOT B) OR B OR C)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testInputInvert2() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/inputInvert2.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:1 defines (A->1), B:1 defines (B->1), C:1 defines (C->1))\n" +
                "  out(Y:1 reads (Y->1))\n" +
                "  sig()\n" +
                "\n" +
                "  node merged expression\n" +
                "    in(In_2:1 reads (C->1), In_1:1 reads (A->1), In_2:1 reads (B->1))\n" +
                "    out(out:1 defines (Y->1))\n" +
                "    Y->1 := (NOT (A AND B) OR C)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testSplitter() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/splitter.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:4 defines (A->1))\n" +
                "  out(X:2 reads (X->1))\n" +
                "  sig(s0->1, s1->1)\n" +
                "\n" +
                "  node Splitter\n" +
                "    in(0-3:4 reads (A->1))\n" +
                "    out(0,1:2 defines (s0->1), 2,3:2 defines (s1->1))\n" +
                "    s0 := A(0-1)\n" +
                "    s1 := A(2-3)\n" +
                "  node merged expression\n" +
                "    in(In_1:2 reads (s0->1), in:2 reads (s1->1))\n" +
                "    out(out:2 defines (X->1))\n" +
                "    X->1 := (s0 AND NOT s1)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testSplitter2() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/splitter2.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:2 defines (A->1), B:2 defines (B->1))\n" +
                "  out(X:1 reads (X->1), Y:3 reads (Y->1))\n" +
                "  sig(s0->1)\n" +
                "\n" +
                "  node Splitter\n" +
                "    in(0,1:2 reads (A->1), 2,3:2 reads (B->1))\n" +
                "    out(single:4 defines (s0->1))\n" +
                "    s0(0-1) := A\n" +
                "    s0(2-3) := B\n" +
                "  node Splitter\n" +
                "    in(single:4 reads (s0->1))\n" +
                "    out(0:1 defines (X->1), 1-3:3 defines (Y->1))\n" +
                "    X := s0(0-0)\n" +
                "    Y := s0(1-3)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testClock() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/clock.dig", new ClockIntegratorGeneric(10));
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(A:1 defines (A->1), C:1 defines (C->1))\n" +
                "  out(X:1 reads (X->1))\n" +
                "  sig(s0->1)\n" +
                "\n" +
                "  node simpleClockDivider\n" +
                "    in(cin:1 reads (C->1))\n" +
                "    out(cout:1 defines (s0->1))\n" +
                "  node D_FF\n" +
                "    in(D:1 reads (A->1), C:1 reads (s0->1))\n" +
                "    out(Q:1 defines (X->1), ~Q:1 is not used)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

    public void testNaming() throws IOException, PinException, HDLException, NodeException, ElementNotFoundException {
        HDLCircuit hdl = getCircuit("dig/hdl/model2/naming.dig", null);
        hdl.mergeOperations().nameNets();

        CodePrinterStr cp = new CodePrinterStr();
        hdl.print(cp);
        assertEquals("circuit main\n" +
                "  in(S0:1 defines (S0->2), S1:1 defines (S1->2))\n" +
                "  out(S2:1 reads (S2->1), S3:1 reads (S3->1))\n" +
                "  sig(s4->2)\n" +
                "\n" +
                "  node NOr\n" +
                "    in(In_1:1 reads (S0->2), In_2:1 reads (S1->2))\n" +
                "    out(out:1 defines (s4->2))\n" +
                "    s4->2 := NOT (S0 OR S1)\n" +
                "  node XOr\n" +
                "    in(In_1:1 reads (S0->2), In_2:1 reads (s4->2))\n" +
                "    out(out:1 defines (S2->1))\n" +
                "    S2->1 := (S0 XOR s4)\n" +
                "  node XOr\n" +
                "    in(In_1:1 reads (s4->2), In_2:1 reads (S1->2))\n" +
                "    out(out:1 defines (S3->1))\n" +
                "    S3->1 := (s4 XOR S1)\n" +
                "\n" +
                "end circuit main\n", cp.toString());
    }

}