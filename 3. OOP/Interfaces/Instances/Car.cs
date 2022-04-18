using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Interfaces.interfaces;

namespace Interfaces.Instances
{
    internal class Car : IMove
    {
        IFrame frame;
        IEngine engine;
        string name;

        public Car(string name ,IFrame frame,IEngine engine)
        {
            this.frame = frame;
            this.engine = engine;
            this.name = name;
        }
        public IFrame GetFrame()
        {
            return frame;
        }
        public string Move(String[] initMessages)
        {
            /*
            bool[] check = Status(initMessages);
            for(int i = 0;i<initMessages.Length;i++)
            {
                if(!check[i])
                {
                    return initMessages[i];
                }
            }

            if(engine is Engines.BoatEngine)
            {
                return "Вы поехали... и не уехали :), вы упали вместе с мотоциклом";
            }

            

            if(frame is Frames.FrameMotocycle)
            {
                if(engine is Engines.EnginePowerful)
                {
                    return "Вы поехали... и слетели с транспорта :)";
                    
                }
                if(engine is Engines.AirCushionPropeller)
                {
                    return "Вы поехали... и вас сдуло :)";
                }
            }
            else
            {
               
            }


            return "Вы спокойно доехали до места назначения.";
            */
            return "";

        }
        public IDictionary<string,string[]> Status()
        {
            Dictionary<string,string[]> results = new Dictionary<string,string[]>();
            List<string> temp = new List<string>();
            foreach(IWheel wheel in frame.GetWheels())
            {
                if (wheel.GetStatus() != "")
                {
                    temp.Add(wheel.GetStatus());
                }
                else
                {
                    temp.Add("OK");
                }
            }
            results.Add("wheels", temp.ToArray());
            temp.Clear();
            foreach (IDoor door in frame.GetDoors())
            {
                if (door.GetStatus() != "")
                {
                    temp.Add(door.GetStatus());
                }
                else
                {
                    temp.Add("OK");
                }
            }
            results.Add("doors", temp.ToArray());
            results.Add("frame", new string[]
            {
                frame.GetStatus()
            });
            results.Add("engine", new string[]
            {
                engine.GetStatus()
            });
            return results;
        }
        public string GetName()
        {
            return name;
        }
    }
}
