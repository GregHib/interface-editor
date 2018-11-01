package com.greg.view.hierarchy

import org.junit.Assert
import org.junit.Test
import tornadofx.observable

class DragTreeCellTest {
    enum class ParentType {
        ROOT,
        CONTAINER;
    }

    enum class WidgetType {
        CONTAINER,
        WIDGET;
    }

    class DragTreeTest(val widget: WidgetType, val parent: ParentType)

    @Test
    fun test() {
        val list = mutableListOf<Int>(0, 1, 2).observable()
        list.remove(1)
        list.remove(2)
        list.addAll(listOf(1, 2))
    }
    @Test
    fun combinations() {
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT), DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT), DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT), DragTreeTest(WidgetType.WIDGET, ParentType.ROOT))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT), DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER))

        test(DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER), DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER), DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER), DragTreeTest(WidgetType.WIDGET, ParentType.ROOT))
        test(DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER), DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER))

        test(DragTreeTest(WidgetType.WIDGET, ParentType.ROOT), DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.ROOT), DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.ROOT), DragTreeTest(WidgetType.WIDGET, ParentType.ROOT))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.ROOT), DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER))

        test(DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER), DragTreeTest(WidgetType.CONTAINER, ParentType.ROOT))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER), DragTreeTest(WidgetType.CONTAINER, ParentType.CONTAINER))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER), DragTreeTest(WidgetType.WIDGET, ParentType.ROOT))
        test(DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER), DragTreeTest(WidgetType.WIDGET, ParentType.CONTAINER))

    }

    private fun test(select: DragTreeTest, target: DragTreeTest) {
        println("${select.widget} ${select.parent} ${target.widget} ${target.parent}")
        /*
            if target widget is container
                if target is container select is in
                    move select to end

                if target parent is root
                    delete select from parent
                    add select to widgets

                if target parent is container
                    delete select from parent
                    add select to target parent

            if target widget is widget

                if target parent is root
                    if select parent is root
                        move select to end (just hierarchies)

                    if select parent is container
                        delete select from parent
                        add select to widgets

                if target parent is container
                    if select parent is root
                        widgets remove select
                        target parent add select at index of target

                    if select parent is container
                        if select parent is target parent
                            move select to index of target
                        else
                            select parent remove select
                            target parent add select at index of target
             */

        //If target is a container
        if (target.widget == WidgetType.CONTAINER) {

            if (/* target == select.parent */select.parent == ParentType.CONTAINER) {
                //select.parent.widget.children.move(select.widget, select.parent.widget.children.length -1)
                Assert.assertTrue(true)
                return
            } else {
                if (select.parent == ParentType.ROOT) {
                    //widgets.remove(select.widget)
                } else if (select.parent == ParentType.CONTAINER) {
                    //select.parent.widget.children.remove(select.widget)
                }

//                if (target.parent == ParentType.ROOT) {
                    //widgets.children.add(select.widget)
//                } else if (target.parent == ParentType.CONTAINER) {
                    //target.parent.widget.children.add(select.widget)
//                }
                Assert.assertTrue(true)
                return
            }
        } else if (target.widget == WidgetType.WIDGET) {
            if (select.parent == ParentType.CONTAINER && target.parent == ParentType.CONTAINER && select.parent == target.parent) {
                //select.parent.widget.children.move(select.widget, select.parent.widget.children.indexOf(target.widget))
                Assert.assertTrue(true)
                return
            } else if (select.parent == ParentType.ROOT && target.parent == ParentType.ROOT) {
                //select.parent.children.move(select, select.parent.children.indexOf(target)) (just hierarchy changed)
                Assert.assertTrue(true)
                return
            }

            if (select.parent == ParentType.CONTAINER) {
                //select.parent.widget.children.remove(select.widget)
            } else if (select.parent == ParentType.ROOT && target.parent == ParentType.CONTAINER) {
                //widgets.remove(select.widget)
            }

            //target.parent.widget.children.add(select.widget, target.parent.widget.children.indexOf(target.widget))
            Assert.assertTrue(true)
            return
        }
        Assert.fail()
    }

    /*
    if(target.parent == ParentType.ROOT) {
                if(select.parent == ParentType.ROOT) {
                    //select.parent.children.move(select, select.parent.children.indexOf(target)) (just hierarchy changed)
                    Assert.assertTrue(true)
                    return
                } else if(select.parent == ParentType.CONTAINER) {
                    //select.parent.widget.children.remove(select.widget)
                    //target.parent.widget.children.add(select.widget, target.parent.widget.children.indexOf(target.widget))
                    Assert.assertTrue(true)
                    return
                }
            } else if(target.parent == ParentType.CONTAINER) {
                if(select.parent == ParentType.ROOT) {
                    //widgets.remove(select.widget)
                    //target.parent.widget.children.add(select.widget, target.parent.widget.children.indexOf(target.widget))
                    Assert.assertTrue(true)
                    return
                } else if(select.parent == ParentType.CONTAINER) {
                    if(select.parent == target.parent) {
                        //select.parent.widget.children.move(select.widget, select.parent.widget.children.indexOf(target.widget))
                        Assert.assertTrue(true)
                        return
                    } else {
                        //select.parent.widget.children.remove(select.widget)
                        //target.parent.widget.children.add(select.widget, target.parent.widget.children.indexOf(target.widget))
                        Assert.assertTrue(true)
                        return
                    }
                }
            }
     */
}