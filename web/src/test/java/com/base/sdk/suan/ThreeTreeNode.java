package com.base.sdk.suan;

public class ThreeTreeNode {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        TreeNode l1 = new TreeNode(1);
        TreeNode r1 = new TreeNode(9);
        TreeNode l2 = new TreeNode(8);
        TreeNode m2 = new TreeNode(2);
        TreeNode r2 = new TreeNode(7);
        TreeNode r3 = new TreeNode(6);
        TreeNode l4 = new TreeNode(5);
        root.left = l1;
        root.right = r1;
        l1.left = l2;
        l1.middle = m2;
        l1.right = r2;
        r1.right = r3;
        m2.left = l4;

        reverse(root);

        System.out.println(root);
    }

    public static void reverse(TreeNode root) {
        if (root == null) {
            return;
        }
        TreeNode tmp = root.left;
        root.left = root.right;
        root.right = tmp;
        reverse(root.left);
        reverse(root.middle);
        reverse(root.right);
    }

    public static class TreeNode {
        int      val;
        TreeNode left;
        TreeNode middle;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }
}
