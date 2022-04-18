using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;
using Interfaces.utils;
namespace Interfaces.Instances
{
    internal class Builder : IBuilder
    {
        private Part[] engines = new Part[]
        {
            new Part("Слабый двигатель",typeof(Instances.Engines.EngineSmall)),
            new Part("Средний двигатель",typeof(Instances.Engines.EngineMedium)),
            new Part("Мощный двигатель",typeof(Instances.Engines.EnginePowerful)),
            new Part("Лодочный мотор", typeof(Instances.Engines.BoatEngine)),
            new Part("Пропеллер", typeof(Instances.Engines.AirCushionPropeller))
        };
        private Part[] frames = new Part[]
        {
            new Part("Рама для машины",typeof(Instances.Frames.FrameCar)),
            new Part("Рама для мотоцикла",typeof(Instances.Frames.FrameMotocycle))
        };
        private Part[] wheels = new Part[]
        {
            new Part("Маленькое колесо",typeof(Instances.Wheels.WheelSmall)),
            new Part("Среднее колесо",typeof(Instances.Wheels.WheelMedium)),
            new Part("Большое колесо",typeof(Instances.Wheels.WheelBig))
        };
        private Part[] bottoms = new Part[]
        {
            new Part("Маленький корпус",typeof(Instances.Bottoms.BottomSmall)),
            new Part("Средний корпус",typeof(Instances.Bottoms.BottomMedium)),
            new Part("Большой корпус",typeof(Instances.Bottoms.BottomBig))
        };
        private Part[] doors = new Part[]
        {
            new Part("Обычная левостороняя дверь",typeof(Instances.Doors.DoorLeft)),
            new Part("Обычная правостороняя дверь",typeof(Instances.Doors.DoorRight)),
            new Part("Крутая ( :) ) дверь с верхней петлёй",typeof(Instances.Doors.DoorUp)),
            new Part("Дверь-трап",typeof(Instances.Doors.DoorDown))
        };
        private Part[] base_types = new Part[]
        {
            new Part("База автотранспорта",typeof(Instances.Car)),
            new Part("База плавательного средства",typeof(Instances.Boat))
        };

        public IBottom CreateBottom(Part partBottom)
        {
            throw new NotImplementedException();
        }

        public IMove CreateCar(Part partCar)
        {
            IMove move;

            int frame_id = ConsoleUtils.GetNumberAnswer(frames, "Выбор рамы.", "Введите номер рамы: ") - 1;
            IFrame frame = CreateFrame(frames[frame_id]);
            int engine_id = ConsoleUtils.GetNumberAnswer(engines, "Выбор двигателя.", "Введите номер двигателя: ") - 1;
            IEngine engine = CreateEngine(engines[engine_id]);
            move = (IMove)Activator.CreateInstance(partCar.Type, new object[]
            {
                partCar.Name,
                frame,
                engine
            });
            return move;
        }

        public IDoor CreateDoor(Part partDoor)
        {
            IDoor door = (IDoor) Activator.CreateInstance(partDoor.Type, new object[]
            {
                partDoor.Name,
                new Check()
            });
            door.init(new Dictionary<string, object>());
            Console.WriteLine("Дверь добавлена!");
            return door;
        }

        public IEngine CreateEngine(Part partEngine)
        {
            ConsoleUtils.PrintInFrame("Создание двигателя.");
            IEngine engine = (IEngine)Activator.CreateInstance(partEngine.Type, new object[]
            {
                partEngine.Name,
                new Check()
            });
            int max_power = engine.GetMaxPower();
            int min_power = engine.GetMinPower();
            string input_line = "";
            if (max_power != Int32.MaxValue && min_power != Int32.MinValue)
            {
                input_line = "Желаемая мощность (рекомендуемые значения: " + min_power + "<x<" + max_power + "): ";
            }
            else if (min_power != Int32.MinValue)
            {
                input_line = "Желаемая мощность (рекомендуемые значения: >" + min_power + "): ";
            }
            else if (max_power != Int32.MaxValue)
            {
                input_line = "Желаемая мощность (рекомендуемые значения: <" + max_power + "): ";
            }
            else
            {
                input_line = "Желаемая мощность: ";
            }
            Dictionary<string, object> init_values = new Dictionary<string, object>();
            init_values.Add("power", ConsoleUtils.GetEnterNumber(Int32.MinValue, Int32.MinValue, input_line, "Неверная мощность!"));
            engine.init(init_values);
            Console.WriteLine("---------------------------------------------------------------");
            return engine;
        }

        public IFrame CreateFrame(Part partFrame)
        {
            ConsoleUtils.PrintInFrame("Создание рамы.");
            IFrame frame = (IFrame)Activator.CreateInstance(partFrame.Type, new object[]
            {
                partFrame.Name,
                new Check()

            });
            List<IWheel> wheels = new List<IWheel>();
            List<IDoor> doors = new List<IDoor>();
            while (true)
            {
                string outputWheels = "Количество колёс: " + wheels.Count;
                string outputDoors = "Количество дверей: " + doors.Count;
                string unlimited = "";
                if (frame.getUnlimitedDoorsAndWheels())
                {
                    unlimited = ">=";
                }
                if (frame.GetCountWheels() != 0)
                {
                    if (wheels.Count < frame.GetCountWheels())
                    {
                        outputWheels += ", рекомендовано: " + unlimited + frame.GetCountWheels();
                    }
                    Console.WriteLine(outputWheels);
                }
                if (frame.GetCountDoors() != 0)
                {
                    if (doors.Count < frame.GetCountDoors())
                    {
                        outputDoors += ", рекомендовано: " + unlimited + frame.GetCountDoors();
                    }
                    Console.WriteLine(outputDoors);
                }
                Console.WriteLine("Что вы хотите сделать?\n1. Добавить колёса;\n2. Добавить двери;\n3. Продолжить создание транспорта;");
                int command = ConsoleUtils.GetEnterNumber(3);
                if (command == 3)
                {
                    break;
                }
                if (command == 1)
                {
                    do
                    {
                        int type_wheel = ConsoleUtils.GetNumberAnswer(this.wheels, "Выбор типа колеса", "Введите номер: ")-1;
                        wheels.Add(CreateWheel(this.wheels[type_wheel]));
                        Console.WriteLine("Что вы хотите сделать?\n1. Добавить ещё одно колесо;\n2. Продолжить создание рамы;");
                        command = ConsoleUtils.GetEnterNumber(2);
                    } while (command !=2);
                }
                else
                {
                    do
                    {
                        int type_door = ConsoleUtils.GetNumberAnswer(this.doors, "Выбор типа двери", "Введите номер: ") - 1;
                        doors.Add(CreateDoor(this.doors[type_door]));
                        Console.WriteLine("Что вы хотите сделать?\n1. Добавить ещё одну дверь;\n2. Продолжить создание рамы;");
                        command = ConsoleUtils.GetEnterNumber(2);
                    } while (command != 2);
                }
            }
            IDictionary<string,object> init_values = new Dictionary<string,object>();
            init_values.Add("wheels", wheels.ToArray());
            init_values.Add("doors", doors.ToArray());
            frame.init(init_values);
            Console.WriteLine("---------------------------------------------------------------");
            return frame;
        }

        public IWheel CreateWheel(Part partWheel)
        {
            IWheel wheel = (IWheel)Activator.CreateInstance(partWheel.Type, new object[]
            {
                partWheel.Name,
                new Check()
            });

            int pressure = ConsoleUtils.GetEnterNumber(
                int.MaxValue,
                int.MinValue,
                "Желаемое давление (рекомендованное: " + wheel.GetMinPressure() + "<x<" + wheel.GetMaxPressure() + "): ",
                "Неверное давление!");
            IDictionary<string, object> init_values = new Dictionary<string, object>();
            init_values.Add("pressure", (float)pressure);
            wheel.init(init_values);
            Console.WriteLine("Колесо добавлено!");
            return wheel;
        }

        public Part[] GetBaseTypes()
        {
            return base_types;
        }

        public Part[] GetBottomsTypes()
        {
            return bottoms;
        }

        public Part[] GetDoorsTypes()
        {
            return doors;
        }

        public Part[] GetEnginesTypes()
        {
            return engines;
        }

        public Part[] GetFramesTypes()
        {
            return frames;
        }

        public Part[] GetWheelsTypes()
        {
            return wheels;
        }
    }
}
