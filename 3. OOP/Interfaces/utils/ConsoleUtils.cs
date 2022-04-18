using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.Instances;

namespace Interfaces.utils
{
    internal static class ConsoleUtils
    {
        public static void PrintInFrame(string text)
        {
            char angle_0_0 = '┌';
            char angle_0_1 = '┐';
            char angle_1_0 = '└';
            char angle_1_1 = '┘';
            char hor_line = '─';
            char ver_line  = '│';
            Console.Write(angle_0_0);
            for(int i = 0;i<text.Length;i++)
            {
                Console.Write(hor_line);
            }
            Console.WriteLine(angle_0_1);
            Console.Write(ver_line);
            Console.Write(text);
            Console.WriteLine(ver_line);
            Console.Write(angle_1_0);
            for (int i = 0; i < text.Length; i++)
            {
                Console.Write(hor_line);
            }
            Console.WriteLine(angle_1_1);
        }
        public static int GetNumberAnswer(Part[] parts, string main_string, string select_string)
        {
            PrintInFrame(main_string);
            Console.WriteLine("Возможные варианты:");
            for (int i = 0; i < parts.Length; i++)
            {
                Console.WriteLine("\t" + (i + 1) + ". " + parts[i].Name + ";");
            }
            return GetEnterNumber(parts.Length, input_string: select_string, error_string: "Неверный номер!");
        }
        public static int GetEnterNumber(int max, int min = 1, string input_string = "Введите номер команды: ", string error_string = "Неверный номер команды!")
        {
            while (true)
            {
                Console.Write(input_string);
                int command = -1;
                try
                {
                    command = Convert.ToInt32(Console.ReadLine());
                }
                catch (Exception ex)
                {
                    Console.WriteLine(error_string);
                    continue;
                }
                if (command < min || command > max)
                {
                    Console.WriteLine(error_string);
                    continue;
                }
                else
                {
                    return command;
                }
            }
        }
    }
}
