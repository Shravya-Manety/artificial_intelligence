import itertools
from typing import List
from collections import deque
import heapq
import math
import time


class Solution:
    algorithm = ['BFS', 'UCS', 'A*']
    w_columns, h_rows = 0, 0
    x_landing, y_landing = 0, 0
    initial_pos = (x_landing, y_landing)
    elevation = 0
    num_target_sites = 0
    list_target_sites = []
    coordinate_matrix = []

    def take_input_prepare_output(self):

        input_file = open("input.txt", "r")
        list_of_lines = input_file.readlines()
        input_file.close()

        with open("output.txt", 'w+') as output_file:
            line = 0

            if not list_of_lines:
                output_file.write("FAIL")

            exe_method, line = self.algorithm.index(
                list_of_lines[line].strip()), line + 1 if output_file.read() is not "FAIL\n" and list_of_lines[
                line].strip() in self.algorithm else output_file.write("FAIL")

            if output_file.read() is not "FAIL\n":
                temp_list = list_of_lines[line].strip().split()
                self.w_columns = int(temp_list[0])
                self.h_rows = int(temp_list[1])
                if self.w_columns < 0 or self.h_rows < 0:
                    output_file.write("FAIL")
                else:
                    line += 1

            if output_file.read() is not "FAIL\n":
                temp_list = list_of_lines[line].strip().split()
                self.x_landing = int(temp_list[0])
                self.y_landing = int(temp_list[1])
                if self.x_landing < 0 or self.y_landing < 0 or self.x_landing > self.w_columns or self.y_landing > self.h_rows:
                    output_file.write("FAIL")
                else:
                    line += 1

            self.initial_pos = (self.x_landing, self.y_landing)
            self.elevation, line = int(
                list_of_lines[line].strip()), line + 1 if output_file.read() is not "FAIL\n" and int(
                list_of_lines[line].strip()) >= 0 else output_file.write("FAIL")
            self.num_target_sites, line = int(
                list_of_lines[line].strip()), line + 1 if output_file.read() is not "FAIL\n" and int(
                list_of_lines[line].strip()) >= 0 else output_file.write("FAIL")

            for i in range(0, self.num_target_sites):
                self.list_target_sites.append(tuple(map(int, list_of_lines[line].strip().split())))
                line += 1

            for i in range(0, self.h_rows):
                self.coordinate_matrix.append(list(map(int, list_of_lines[line].strip().split())))
                line += 1

            if exe_method == 0:
                ans_list = self.breadth_first_search()
            elif exe_method == 1:
                ans_list = self.uniform_cost_search()
            else:
                ans_list = self.a_star()

            for i in range(len(ans_list)):
                if ans_list[i] == ['FAIL']:
                    if i == len(ans_list)-1:
                        output_file.write("{}".format(ans_list[i][0]))
                    else:
                        output_file.write("{}\n".format(ans_list[i][0]))
                else:
                    values = ans_list[i][::-1]
                    output_line = ""
                    for val in values:
                        output_line += " " + val
                    if i == len(ans_list) - 1:
                        output_file.write("{}".format(output_line.strip()))
                    else:
                        output_file.write("{}\n".format(output_line.strip()))

    def breadth_first_search(self) -> List[List[str]]:

        list_of_paths = []
        child_parent_dict = {}
        get_child = [(-1, -1), (0, -1), (1, -1), (1, 0), (1, 1), (0, 1), (-1, 1), (-1, 0)]

        for target in self.list_target_sites:

            if self.initial_pos == target:
                templist = []
                templist.append("{},{}".format(str(self.y_landing), str(self.x_landing)))
                list_of_paths.append(templist)
                continue

            if target in child_parent_dict:
                child_node = target
                templist = []
                while child_node != self.initial_pos:
                    templist.append("{},{}".format(str(child_node[0]), str(child_node[1])))
                    child_node = child_parent_dict.get(child_node)
                templist.append("{},{}".format(str(child_node[0]), str(child_node[1])))
                list_of_paths.append(templist)
                continue

            z = 0
            queue = deque()
            entries = {}
            queue.append(self.initial_pos)
            entries[self.initial_pos] = 1
            explored_nodes = {}

            while True:
                if not queue:
                    templist = ['FAIL']
                    list_of_paths.append(templist)
                    break
                node = queue.popleft()
                del entries[node]
                explored_nodes[node] = 1

                for i in range(len(get_child)):
                    j_child = get_child[i][0] + node[0]
                    i_child = get_child[i][1] + node[1]
                    child_node = (j_child, i_child)

                    if 0 <= j_child < self.w_columns and 0 <= i_child < self.h_rows and \
                            abs(self.coordinate_matrix[node[1]][node[0]] - self.coordinate_matrix[i_child][
                                j_child]) <= self.elevation and child_node not in entries and child_node not in explored_nodes:

                        if child_node == target:
                            templist = []
                            while child_node != self.initial_pos:
                                templist.append("{},{}".format(str(child_node[0]), str(child_node[1])))
                                child_parent_dict[(j_child, i_child)] = node
                                child_node = child_parent_dict.get(child_node)
                            templist.append("{},{}".format(str(child_node[0]), str(child_node[1])))
                            list_of_paths.append(templist)
                            z = 1
                            break

                        queue.append(child_node)
                        entries[child_node] = 1
                        child_parent_dict[child_node] = node
                if z:
                    break
        return list_of_paths

    def uniform_cost_search(self) -> List[List[str]]:

        list_of_paths = []
        child_parent_dict = {}
        get_child = [(-1, -1), (0, -1), (1, -1), (1, 0), (1, 1), (0, 1), (-1, 1), (-1, 0)]
        entries = {}
        counter = itertools.count()

        def add_node(node, path_cost, parent, priority_queue):
            count_val = next(counter)
            entry = [path_cost, count_val, node]
            entries[node] = entry
            heapq.heappush(priority_queue, entry)
            child_parent_dict[node] = parent

        def pop_node(priority_queue):
            while priority_queue:
                path_cost, count, node = heapq.heappop(priority_queue)
                if node in entries:
                    del entries[node]
                    return path_cost, node

        for target in self.list_target_sites:

            priority_queue, explored_nodes, entries, child_parent_dict= [], {}, {}, {}
            add_node(self.initial_pos, 0, self.initial_pos, priority_queue)

            while True:
                if not priority_queue:
                    templist = ['FAIL']
                    list_of_paths.append(templist)
                    break

                path_cost, node = pop_node(priority_queue)

                if node == target:
                    templist = []
                    while node != self.initial_pos:
                        templist.append("{},{}".format(str(node[0]), str(node[1])))
                        node = child_parent_dict.get(node)
                    templist.append("{},{}".format(str(node[0]), str(node[1])))
                    list_of_paths.append(templist)
                    break
                explored_nodes[node] = path_cost

                for i in range(len(get_child)):

                    j_child = get_child[i][0] + node[0]
                    i_child = get_child[i][1] + node[1]
                    child_node = (j_child, i_child)

                    if 0 <= j_child < self.w_columns and 0 <= i_child < self.h_rows and \
                            abs(self.coordinate_matrix[node[1]][node[0]] - self.coordinate_matrix[i_child][
                                j_child]) <= self.elevation:
                        child_path_cost = path_cost + 14 if i % 2 == 0 else path_cost + 10

                        if child_node not in entries and child_node not in explored_nodes:
                            add_node(child_node, child_path_cost, node, priority_queue)
                        elif child_node in entries:
                            if entries[child_node][0] > child_path_cost:
                                count_val = next(counter)
                                entries[child_node] = [child_path_cost, count_val, child_node]
                                priority_queue = list(entries.values())
                                heapq.heapify(priority_queue)
                                child_parent_dict[child_node] = node

        return list_of_paths

    def a_star(self) -> List[List[str]]:

        list_of_paths = []
        child_parent_dict = {}
        get_child = [(-1, -1), (0, -1), (1, -1), (1, 0), (1, 1), (0, 1), (-1, 1), (-1, 0)]
        entries = {}
        counter = itertools.count()

        def calculate_heuristic(node):
            dy = abs(node[0] - target[0])
            dx = abs(node[1] - target[1])
            return round(math.sqrt(dx * dx + dy * dy))

        def add_node(node, path_cost, parent, f_cost, priority_queue):
            count_val = next(counter)
            entry = [f_cost, count_val, node, path_cost]
            entries[node] = entry
            heapq.heappush(priority_queue, entry)
            child_parent_dict[node] = parent

        def pop_node(priority_queue):
            while priority_queue:
                f_cost, count, node, path_cost = heapq.heappop(priority_queue)
                if node in entries:
                    del entries[node]
                    return path_cost, node

        for target in self.list_target_sites:
            priority_queue, explored_nodes, entries = [], {}, {}
            f_cost = calculate_heuristic(self.initial_pos) + 0
            add_node(self.initial_pos, 0, self.initial_pos, f_cost, priority_queue)

            while True:
                if not priority_queue:
                    templist = ['FAIL']
                    list_of_paths.append(templist)
                    break
                path_cost, node = pop_node(priority_queue)

                if node == target:
                    templist = []
                    while node != self.initial_pos:
                        templist.append("{},{}".format(str(node[0]), str(node[1])))
                        node = child_parent_dict.get(node)
                    templist.append("{},{}".format(str(node[0]), str(node[1])))
                    list_of_paths.append(templist)

                    break
                explored_nodes[node] = path_cost

                for i in range(len(get_child)):

                    j_child = get_child[i][0] + node[0]
                    i_child = get_child[i][1] + node[1]

                    if 0 <= j_child < self.w_columns and 0 <= i_child < self.h_rows and \
                            abs(self.coordinate_matrix[node[1]][node[0]] - self.coordinate_matrix[i_child][
                                j_child]) <= self.elevation:

                        elevation_difference = abs(
                            self.coordinate_matrix[node[1]][node[0]] - self.coordinate_matrix[i_child][j_child])
                        child_path_cost = elevation_difference + path_cost + 14 if i % 2 == 0 else elevation_difference + path_cost + 10
                        child_node = (j_child, i_child)
                        f_cost = calculate_heuristic(child_node) + child_path_cost

                        if child_node not in entries and child_node not in explored_nodes:
                            add_node(child_node, child_path_cost, node, f_cost, priority_queue)
                        elif child_node in entries:
                            if entries[child_node][3] > child_path_cost:
                                count_val = next(counter)
                                entries[child_node] = [f_cost, count_val, child_node, child_path_cost]
                                priority_queue = list(entries.values())
                                heapq.heapify(priority_queue)
                                child_parent_dict[child_node] = node
        return list_of_paths


myObj = Solution()
start_time = time.time()
myObj.take_input_prepare_output()
print("total time:", time.time()-start_time)
