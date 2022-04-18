using System;
using System.Collections.Generic;
using Interfaces.Instances;
using Interfaces.interfaces;
using Interfaces.utils;

namespace Interfaces
{
    internal class Program
    {


        private static IMove base_move;
        private static int base_id;
        private static IBuilder builder;

        static void Main(string[] args)
        {

            builder = new Builder();
            ConsoleUtils.PrintInFrame("TransportBuilder v0.1.");
            Console.WriteLine("Добро пожаловать в конструктор транспорта!!!");
            Console.WriteLine("Что бы вы хотели сделать:\n\t1. Создать транспорт;\n\t2. Выйти;");
            int command = ConsoleUtils.GetEnterNumber(2);
            if (command == 2)
            {
                return;
            }
            base_id = ConsoleUtils.GetNumberAnswer(builder.GetBaseTypes(), "Выбор базы.", "Введите номер базы: ") - 1;
            if (BuilderUtils.isCar(builder.GetBaseTypes()[base_id].Type))
            {
                base_move = builder.CreateCar(builder.GetBaseTypes()[base_id]);
            }
            else
            {
                Console.WriteLine("Пока не работает");
                return;
            }
            ConsoleUtils.PrintInFrame("Работа с транспортом.");
            Console.WriteLine("Что хотите сделать?\n\t1. Поехать;\n\t2. Получить статус\n\t3.Выйти");
            command = ConsoleUtils.GetEnterNumber(3);
            if(command == 3)
            {
                return;
            }
            if(command == 1)
            {
                Console.WriteLine("Пока не готово.");
            }
            else
            {
                ConsoleUtils.PrintInFrame("Статус машины");
                string[] temp;
                IDictionary<string, string[]> status = base_move.Status();
                temp = status["wheels"];
                Console.WriteLine("Колёса:");
                for(int i = 0; i<temp.Length;i++)
                {
                    Console.WriteLine("\tКолесо "+i+": "+temp[i]+";");
                }
                temp = status["doors"];
                Console.WriteLine("Двери:");
                for (int i = 0; i < temp.Length; i++)
                {
                    Console.WriteLine("\tДверь " + i + ": " + temp[i] + ";");
                }
                temp = status["frame"];
                Console.WriteLine("Рамы:");
                for (int i = 0; i < temp.Length; i++)
                {
                    Console.WriteLine("\tРама " + i + ": " + temp[i] + ";");
                }
                temp = status["engine"];
                Console.WriteLine("Двигатели:");
                for (int i = 0; i < temp.Length; i++)
                {
                    Console.WriteLine("\tДвигатель " + i + ": " + temp[i] + ";");
                }
            }


            





        }
    }
}

