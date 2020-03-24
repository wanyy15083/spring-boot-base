package com.base.sdk.suan;

public class TowSum {
    public static void main(String[] args) {
        ListNode l11 = new ListNode(2);
        ListNode l12 = new ListNode(4);
        ListNode l13 = new ListNode(3);
        l11.next = l12;
        l12.next = l13;

        ListNode l21 = new ListNode(7);
        ListNode l22 = new ListNode(6);
        ListNode l23 = new ListNode(8);
        l21.next = l22;
        l22.next = l23;

        System.out.println(addTwoNumbers(l11,l21));
    }

    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = new ListNode(0);
        ListNode curr = result;
        int carry = 0;
        while (l1 != null || l2 != null) {
            int a=l1 == null?0:l1.val;
            int b=l2 == null?0:l2.val;
            int sum = a+b+carry;
            carry = sum/10;
            curr.next = new ListNode(sum%10);
            curr = curr.next;
            if(l1!=null) {
                l1 = l1.next;
            }
            if(l2!=null) {
                l2 = l2.next;
            }
        }
        if (carry == 1) {
            curr.next = new ListNode(carry);
        }
        return result;
    }
}
