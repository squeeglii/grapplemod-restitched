package com.yyon.grapplinghook.util.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.*;
import java.util.function.Supplier;

public class ModelPath {

    public static Supplier<Iterator<String>> ROOT_TO_LEFT_LEG = () -> List.of("left_leg").iterator();
    public static Supplier<Iterator<String>> ROOT_TO_RIGHT_LEG = () -> List.of("right_leg").iterator();

    public static Supplier<Iterator<String>> combine(Supplier<Iterator<String>> path, String... continued) {
        LinkedList<String> newPath = new LinkedList<>();
        path.get().forEachRemaining(newPath::add);
        newPath.addAll(Arrays.asList(continued));

        return newPath::iterator;
    }


    public static PartDefinition goTo(MeshDefinition mesh, Iterator<String> path) {
        return goTo(mesh.getRoot(), path);
    }

    public static PartDefinition goTo(PartDefinition root, Iterator<String> path) {
        return path.hasNext()
                ? goTo(root.getChild(path.next()), path)
                : root;
    }

    public static ModelPart goTo(ModelPart root, Iterator<String> path) {
        return path.hasNext()
                ? goTo(root.getChild(path.next()), path)
                : root;
    }

}
